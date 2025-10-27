Feature: Regression tests for the detail REST API

  Background:
    * url baseUrl
    * def TestDataManager = Java.type('com.example.karate.support.TestDataManager')
    * eval TestDataManager.reset()

  Scenario: List details for the login user in descending order
    # ログインユーザー user1 の明細が最新順で 3 件返ることを確認する
    Given path 'details'
    And param userId = 'user1'
    When method get
    Then status 200
    And match response == '#[3]'
    And match response contains deep { title: '交通費精算', status: '下書き', detailId: '#number' }
    And match response contains deep { title: '出張費', status: '下書き', detailId: '#number' }
    And match response contains deep { title: '備品購入', status: '申請中', detailId: '#number' }
    * def ids = response[*].detailId
    * def sorted = karate.clone(ids)
    * eval sorted.sort(function(a, b){ return b - a; })
    * match ids == sorted

  Scenario: Filter details by status parameter
    # ステータス指定で下書きの明細だけを取得できることを検証する
    Given path 'details'
    And param userId = 'user1'
    And param status = 'draft'
    When method get
    Then status 200
    And match response == '#[2]'
    And match each response == { detailId: '#number', title: '#string', status: '下書き' }

  Scenario: Reject unknown status values
    # 存在しないステータスを指定した場合に 400 エラーになることを確認する
    Given path 'details'
    And param userId = 'user1'
    And param status = 'unknown'
    When method get
    Then status 400
    And match response == { message: 'Unknown status: unknown' }

  Scenario: Apply draft details for the current user
    # 下書き明細を申請するとレスポンスが成功になり、ステータスが申請中へ変わることを確認する
    Given path 'details'
    And param userId = 'user1'
    And param status = 'draft'
    When method get
    Then status 200
    * def targetId = response[0].detailId

    Given path 'details', 'apply'
    And request { userId: 'user1', detailIds: [ #(targetId) ] }
    When method post
    Then status 200
    And match response == { message: 'Request accepted.' }

    Given path 'details'
    And param userId = 'user1'
    And param status = 'requested'
    When method get
    Then status 200
    * def requestedIds = response[*].detailId
    * match requestedIds contains targetId

  Scenario: Reject apply when requesting another user detail
    # 他ユーザーの明細を申請しようとすると拒否されることを検証する
    Given path 'details'
    And param userId = 'user2'
    When method get
    Then status 200
    * def foreignId = response[0].detailId

    Given path 'details', 'apply'
    And request { userId: 'user1', detailIds: [ #(foreignId) ] }
    When method post
    Then status 400
    And match response == { message: '申請対象の明細が見つからない、もしくは権限がありません: [#(foreignId)]' }

  Scenario: Reject apply when the detail is not in draft status
    # 申請対象に下書き以外の明細が含まれる場合はエラーになることを確認する
    Given path 'details'
    And param userId = 'user1'
    And param status = 'requested'
    When method get
    Then status 200
    * def nonDraftId = response[0].detailId

    Given path 'details', 'apply'
    And request { userId: 'user1', detailIds: [ #(nonDraftId) ] }
    When method post
    Then status 400
    And match response == { message: '申請できない状態の明細が含まれています（ID: [#(nonDraftId)]）。' }

  Scenario: Reject apply when no detail IDs are provided
    # 明細 ID を指定しない申請リクエストがエラーになることを確認する
    Given path 'details', 'apply'
    And request { userId: 'user1', detailIds: [] }
    When method post
    Then status 400
    And match response == { message: '申請する明細を1件以上選択してください。' }
