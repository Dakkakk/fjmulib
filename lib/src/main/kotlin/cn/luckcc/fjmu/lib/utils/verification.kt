package cn.luckcc.fjmu.lib.utils

import zx.dkk.utils.callJs

private val verificationCode="""
    function verification(e) {
        function i(e, t) {
            var r = (65535 & e) + (65535 & t);
            return (e >> 16) + (t >> 16) + (r >> 16) << 16 | 65535 & r
        }
        function o(e, t, r, a, n, o) {
            return i(
                function (e, t) {
                    return e << t | e >>> 32 - t
                }(
                    i(
                        i(t, e),
                        i(a, o)
                    ),
                    n
                ),
                r
            )
        }
        function l(e, t, r, a, n, i, l) {
            return o(t & r | ~t & a, e, t, n, i, l)
        }
        function s(e, t, r, a, n, i, l) {
            return o(t & a | r & ~a, e, t, n, i, l)
        }
        function c(e, t, r, a, n, i, l) {
            return o(t ^ r ^ a, e, t, n, i, l)
        }
        function u(e, t, r, a, n, i, l) {
            return o(r ^ (t | ~a), e, t, n, i, l)
        }
        function f(e, t) {
            var r, a, n, o, f;
            e[t >> 5] |= 128 << t % 32, e[14 + (t + 64 >>> 9 << 4)] = t;
            var d = 1732584193, h = -271733879, p = -1732584194, b = 271733878;
            for (r = 0; r < e.length; r += 16)
                a = d, n = h, o = p, f = b, h = u(h = u(h = u(h = u(h = c(h = c(h = c(h = c(h = s(h = s(h = s(h = s(h = l(h = l(h = l(h = l(h, p = l(p, b = l(b, d = l(d, h, p, b, e[r], 7, -680876936), h, p, e[r + 1], 12, -389564586), d, h, e[r + 2], 17, 606105819), b, d, e[r + 3], 22, -1044525330), p = l(p, b = l(b, d = l(d, h, p, b, e[r + 4], 7, -176418897), h, p, e[r + 5], 12, 1200080426), d, h, e[r + 6], 17, -1473231341), b, d, e[r + 7], 22, -45705983), p = l(p, b = l(b, d = l(d, h, p, b, e[r + 8], 7, 1770035416), h, p, e[r + 9], 12, -1958414417), d, h, e[r + 10], 17, -42063), b, d, e[r + 11], 22, -1990404162), p = l(p, b = l(b, d = l(d, h, p, b, e[r + 12], 7, 1804603682), h, p, e[r + 13], 12, -40341101), d, h, e[r + 14], 17, -1502002290), b, d, e[r + 15], 22, 1236535329), p = s(p, b = s(b, d = s(d, h, p, b, e[r + 1], 5, -165796510), h, p, e[r + 6], 9, -1069501632), d, h, e[r + 11], 14, 643717713), b, d, e[r], 20, -373897302), p = s(p, b = s(b, d = s(d, h, p, b, e[r + 5], 5, -701558691), h, p, e[r + 10], 9, 38016083), d, h, e[r + 15], 14, -660478335), b, d, e[r + 4], 20, -405537848), p = s(p, b = s(b, d = s(d, h, p, b, e[r + 9], 5, 568446438), h, p, e[r + 14], 9, -1019803690), d, h, e[r + 3], 14, -187363961), b, d, e[r + 8], 20, 1163531501), p = s(p, b = s(b, d = s(d, h, p, b, e[r + 13], 5, -1444681467), h, p, e[r + 2], 9, -51403784), d, h, e[r + 7], 14, 1735328473), b, d, e[r + 12], 20, -1926607734), p = c(p, b = c(b, d = c(d, h, p, b, e[r + 5], 4, -378558), h, p, e[r + 8], 11, -2022574463), d, h, e[r + 11], 16, 1839030562), b, d, e[r + 14], 23, -35309556), p = c(p, b = c(b, d = c(d, h, p, b, e[r + 1], 4, -1530992060), h, p, e[r + 4], 11, 1272893353), d, h, e[r + 7], 16, -155497632), b, d, e[r + 10], 23, -1094730640), p = c(p, b = c(b, d = c(d, h, p, b, e[r + 13], 4, 681279174), h, p, e[r], 11, -358537222), d, h, e[r + 3], 16, -722521979), b, d, e[r + 6], 23, 76029189), p = c(p, b = c(b, d = c(d, h, p, b, e[r + 9], 4, -640364487), h, p, e[r + 12], 11, -421815835), d, h, e[r + 15], 16, 530742520), b, d, e[r + 2], 23, -995338651), p = u(p, b = u(b, d = u(d, h, p, b, e[r], 6, -198630844), h, p, e[r + 7], 10, 1126891415), d, h, e[r + 14], 15, -1416354905), b, d, e[r + 5], 21, -57434055), p = u(p, b = u(b, d = u(d, h, p, b, e[r + 12], 6, 1700485571), h, p, e[r + 3], 10, -1894986606), d, h, e[r + 10], 15, -1051523), b, d, e[r + 1], 21, -2054922799), p = u(p, b = u(b, d = u(d, h, p, b, e[r + 8], 6, 1873313359), h, p, e[r + 15], 10, -30611744), d, h, e[r + 6], 15, -1560198380), b, d, e[r + 13], 21, 1309151649), p = u(p, b = u(b, d = u(d, h, p, b, e[r + 4], 6, -145523070), h, p, e[r + 11], 10, -1120210379), d, h, e[r + 2], 15, 718787259), b, d, e[r + 9], 21, -343485551), d = i(d, a), h = i(h, n), p = i(p, o), b = i(b, f);
            return [d, h, p, b]
        }
        function d(e) {
            var t, r = "", a = 32 * e.length;
            for (t = 0; t < a; t += 8)
                r += String.fromCharCode(e[t >> 5] >>> t % 32 & 255);
            return r
        }
        function h(e) {
            var t, r = [];
            for (r[(e.length >> 2) - 1] = void 0, t = 0; t < r.length; t += 1)
                r[t] = 0;
            var a = 8 * e.length;
            for (t = 0; t < a; t += 8)
                r[t >> 5] |= (255 & e.charCodeAt(t / 8)) << t % 32;
            return r
        }
        function p(e) {
            var t, r, a = "";
            for (r = 0; r < e.length; r += 1)
                t = e.charCodeAt(r), a += "0123456789abcdef".charAt(t >>> 4 & 15) + "0123456789abcdef".charAt(15 & t);
            return a
        }
        function b(e) {
            return unescape(encodeURIComponent(e))
        }
        function v(e) {
            return function (e) {
                return d(f(h(e), 8 * e.length))
            }(b(e))
        }
        function g(e, t) {
            return function (e, t) {
                var r, a, n = h(e), i = [], o = [];
                for (i[15] = o[15] = void 0, n.length > 16 && (n = f(n, 8 * e.length)), r = 0; r < 16; r += 1)
                    i[r] = 909522486 ^ n[r], o[r] = 1549556828 ^ n[r];
                return a = f(i.concat(h(t)), 512 + 8 * t.length), d(f(o.concat(a), 640))
            }(b(e), b(t))
        }
        function m(e, t, r) {
            return t ? r ? g(t, e) :
                function (e, t) {
                    return p(g(e, t))
                }(t, e) : r ? v(e) :
                function (e) {
                    return p(v(e))
                }(e)
        }
        return m(e)
    }
""".trimIndent()
fun verification(param: String): String {
    return callJs(verificationCode, "verification", "verification", param) as String
}