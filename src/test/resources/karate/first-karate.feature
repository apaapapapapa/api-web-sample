Feature: 初めての Karate テスト入門

  Background:
    * url baseUrl

  Scenario: ローカルサーバーから詳細一覧を取得できることを確認する
    # ログインユーザー user1 で明細一覧 API を呼び出し、1 件以上返ることを確認する
    Given path 'details'
    And param userId = 'user1'
    When method get
    Then status 200
    * assert response.length > 0
    # 先頭の要素が ID・タイトル・ステータスを持っていることを検証する
    And match response[0] contains { detailId: '#number', title: '#string', status: '#string' }
