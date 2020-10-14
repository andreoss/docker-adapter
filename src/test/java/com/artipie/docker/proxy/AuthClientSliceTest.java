/*
 * MIT License
 *
 * Copyright (c) 2020 Artipie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.artipie.docker.proxy;

import com.artipie.http.Headers;
import com.artipie.http.Response;
import com.artipie.http.client.auth.AuthClientSlice;
import com.artipie.http.client.auth.Authenticator;
import com.artipie.http.headers.Header;
import com.artipie.http.hm.ResponseMatcher;
import com.artipie.http.rq.RequestLine;
import com.artipie.http.rq.RqMethod;
import com.artipie.http.rs.RsFull;
import com.artipie.http.rs.RsStatus;
import io.reactivex.Flowable;
import java.nio.ByteBuffer;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AuthClientSlice}.
 *
 * @since 0.3
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
class AuthClientSliceTest {

    @Test
    void shouldNotModifyRequestAndResponseIfNoAuthRequired() {
        final String line = new RequestLine(RqMethod.GET, "/file.txt").toString();
        final Header header = new Header("x-name", "some value");
        final byte[] body = "text".getBytes();
        final RsStatus status = RsStatus.OK;
        final Response response = new AuthClientSlice(
            (rsline, rsheaders, rsbody) -> {
                if (!rsline.equals(line)) {
                    throw new IllegalArgumentException(String.format("Line modified: %s", rsline));
                }
                return new RsFull(status, rsheaders, rsbody);
            },
            Authenticator.ANONYMOUS
        ).response(line, new Headers.From(header), Flowable.just(ByteBuffer.wrap(body)));
        MatcherAssert.assertThat(
            response,
            new ResponseMatcher(status, body, header)
        );
    }
}
