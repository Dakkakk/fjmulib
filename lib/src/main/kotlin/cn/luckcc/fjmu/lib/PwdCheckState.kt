package cn.luckcc.fjmu.lib

enum class PwdCheckState(val message:String) {
    Checking("正在查询"),
    NotRunning(""),
    UsernamePasswordNotRight("用户名或密码错误"),
    Correct("密码准确"),
    CaptchaNotRight("无效验证码"),
    CaptchaRequire("请输入验证码"),
    Error("未知错误")
}