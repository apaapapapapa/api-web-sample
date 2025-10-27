package com.example.sample.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import lombok.NoArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * リクエストとレスポンスの文字コードをUTF-8に統一するフィルターです。
 * フォーム送信で文字化けしないよう、全リクエストに適用します。
 */
@WebFilter("/*")
@NoArgsConstructor
public class CharacterEncodingFilter implements Filter {

    /**
     * フィルターチェーンの中でUTF-8を設定し、その後の処理へ制御を渡します。
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;

        if (req.getCharacterEncoding() == null) {
            req.setCharacterEncoding("UTF-8");
        }
        res.setCharacterEncoding("UTF-8");

        chain.doFilter(request, response);
    }
}
