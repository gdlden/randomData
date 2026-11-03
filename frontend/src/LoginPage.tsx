import { FormEvent, useState } from "react"
import { ArrowRight, Eye, EyeOff, LoaderCircle, LockKeyhole, ShieldCheck } from "lucide-react"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { TOKEN_STORAGE_KEY } from "@/lib/auth"

export default function LoginPage() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)

  async function login(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!username.trim() || !password) {
      toast.error("请输入用户名和密码")
      return
    }

    setLoading(true)
    try {
      const response = await fetch("/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: username.trim(), password }),
      })
      if (!response.ok) throw new Error("login failed")
      const token = await response.text()
      if (!token) throw new Error("empty token")
      window.localStorage.setItem(TOKEN_STORAGE_KEY, token)
      toast.success("登录成功")
      window.setTimeout(() => window.location.assign("/app"), 300)
    } catch {
      toast.error("用户名或密码错误")
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="grid min-h-screen bg-background lg:grid-cols-2">
      <section className="relative hidden overflow-hidden border-r bg-zinc-950 p-12 text-white lg:flex lg:flex-col lg:justify-between">
        <div className="absolute inset-0 opacity-[0.08] [background-image:linear-gradient(to_right,#fff_1px,transparent_1px),linear-gradient(to_bottom,#fff_1px,transparent_1px)] [background-size:40px_40px]" />
        <div className="relative flex items-center gap-3">
          <div className="grid size-9 place-items-center rounded-md bg-white text-sm font-bold text-black">R</div>
          <div><div className="text-sm font-semibold">Random Data</div><div className="text-xs text-zinc-400">测试数据生成器</div></div>
        </div>
        <div className="relative max-w-lg">
          <div className="mb-6 grid size-11 place-items-center rounded-lg border border-white/15 bg-white/5"><LockKeyhole className="size-5" /></div>
          <h1 className="text-5xl font-semibold tracking-[-0.05em]">简单的数据工具，<br />从安全登录开始。</h1>
          <p className="mt-6 max-w-md text-sm leading-7 text-zinc-400">登录后即可生成用于开发联调、表单演示和场景验证的人员、企业及产权测试数据。</p>
        </div>
        <div className="relative flex items-center gap-2 text-xs text-zinc-500"><ShieldCheck className="size-4" />JWT 身份验证 · 测试数据不对应真实主体</div>
      </section>

      <section className="flex min-h-screen items-center justify-center px-5 py-10">
        <div className="w-full max-w-sm">
          <div className="mb-8 flex items-center gap-3 lg:hidden">
            <div className="grid size-9 place-items-center rounded-md bg-primary text-sm font-bold text-primary-foreground">R</div>
            <div><div className="text-sm font-semibold">Random Data</div><div className="text-xs text-muted-foreground">测试数据生成器</div></div>
          </div>
          <Card className="border-0 shadow-none sm:border sm:shadow-sm">
            <CardHeader className="px-0 sm:px-6">
              <CardTitle className="text-2xl tracking-tight">欢迎回来</CardTitle>
              <CardDescription>输入账号信息以进入数据工作台</CardDescription>
            </CardHeader>
            <CardContent className="px-0 sm:px-6">
              <form onSubmit={login} className="space-y-5">
                <div className="space-y-2">
                  <Label htmlFor="username">用户名</Label>
                  <Input id="username" name="username" autoComplete="username" autoFocus value={username} onChange={event => setUsername(event.target.value)} placeholder="请输入用户名" className="h-11" />
                </div>
                <div className="space-y-2">
                  <div className="flex items-center justify-between"><Label htmlFor="password">密码</Label><span className="text-xs text-muted-foreground">区分大小写</span></div>
                  <div className="relative">
                    <Input id="password" name="password" type={showPassword ? "text" : "password"} autoComplete="current-password" value={password} onChange={event => setPassword(event.target.value)} placeholder="请输入密码" className="h-11 pr-10" />
                    <Button type="button" variant="ghost" size="icon" onClick={() => setShowPassword(value => !value)} aria-label={showPassword ? "隐藏密码" : "显示密码"} className="absolute right-1 top-1 h-9 w-9 text-muted-foreground">
                      {showPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                    </Button>
                  </div>
                </div>
                <Button type="submit" disabled={loading} className="h-11 w-full">
                  {loading ? <LoaderCircle className="size-4 animate-spin" /> : <ArrowRight className="size-4" />}
                  {loading ? "正在登录" : "登录"}
                </Button>
              </form>
              <p className="mt-6 text-center text-xs leading-5 text-muted-foreground">登录即表示你确认仅将生成内容用于测试与演示。</p>
            </CardContent>
          </Card>
        </div>
      </section>
    </main>
  )
}
