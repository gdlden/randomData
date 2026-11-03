import { useMemo, useState } from "react"
import FingerprintJS from "@fingerprintjs/fingerprintjs"
import { Building2, Check, ChevronDown, Copy, FileScan, LoaderCircle, LogOut, RefreshCw, Sparkles } from "lucide-react"
import { toast } from "sonner"
import LoginPage from "@/LoginPage"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { authHeaders, clearToken, TOKEN_STORAGE_KEY } from "@/lib/auth"

type RandomData = {
  name?: string
  mobile?: string
  idCard?: string
  bankcard?: string
  companyName?: string
  businessScope?: string
  province?: string
  city?: string
  address?: string
  creditCode?: string
  factoryBuildingNo?: string
  propertyCertificateNo?: string
}

type ApiResult<T> = { status: number; message?: string; data: T }
type Status = "idle" | "loading" | "ready" | "error"

function redirectWhenUnauthorized(response: Response) {
  const redirectPath = response.redirected ? new URL(response.url).pathname : ""
  const redirectedToLogin = redirectPath === "/" || redirectPath === "/login"
  if (response.status !== 401 && response.status !== 403 && !redirectedToLogin) return false
  clearToken()
  window.location.assign("/")
  return true
}

const groups = [
  {
    title: "人员信息",
    fields: [
      { key: "name", label: "姓名" },
      { key: "mobile", label: "手机号" },
      { key: "idCard", label: "身份证号", wide: true },
      { key: "bankcard", label: "银行卡号", wide: true },
    ],
  },
  {
    title: "企业信息",
    fields: [
      { key: "companyName", label: "公司名称", wide: true },
      { key: "creditCode", label: "统一社会信用代码", wide: true },
      { key: "province", label: "省份" },
      { key: "city", label: "城市" },
      { key: "address", label: "详细地址", wide: true },
      { key: "businessScope", label: "经营范围", wide: true },
    ],
  },
  {
    title: "产权信息",
    fields: [
      { key: "factoryBuildingNo", label: "厂房编号", wide: true },
      { key: "propertyCertificateNo", label: "不动产权证号", wide: true },
    ],
  },
] as const

async function copyText(value: string) {
  if (navigator.clipboard && window.isSecureContext) {
    await navigator.clipboard.writeText(value)
    return
  }
  const area = document.createElement("textarea")
  area.value = value
  area.style.position = "fixed"
  area.style.opacity = "0"
  document.body.appendChild(area)
  area.select()
  document.execCommand("copy")
  area.remove()
}

function DataField({ id, label, value, wide }: { id: string; label: string; value?: string; wide?: boolean }) {
  const [copied, setCopied] = useState(false)

  async function handleCopy() {
    if (!value) return
    await copyText(value)
    setCopied(true)
    toast.success(`已复制${label}`)
    window.setTimeout(() => setCopied(false), 1200)
  }

  return (
    <div className={wide ? "space-y-1 sm:col-span-2" : "space-y-1"}>
      <Label htmlFor={id} className="text-[11px] text-muted-foreground">{label}</Label>
      <div className="relative">
        <Input id={id} readOnly value={value ?? ""} placeholder="—" className="h-9 pr-9 font-mono text-xs" />
        <Button type="button" variant="ghost" size="icon" onClick={handleCopy} disabled={!value} aria-label={`复制${label}`} className="absolute right-1 top-1 h-7 w-7 text-muted-foreground">
          {copied ? <Check className="size-3.5" /> : <Copy className="size-3.5" />}
        </Button>
      </div>
    </div>
  )
}

function DataWorkspace() {
  const [data, setData] = useState<RandomData>({})
  const [status, setStatus] = useState<Status>("idle")
  const [imageBase64, setImageBase64] = useState("")
  const [imageCode, setImageCode] = useState("")
  const [recognizing, setRecognizing] = useState(false)
  const fingerprintPromise = useMemo(() => FingerprintJS.load(), [])

  const statusText = { idle: "等待生成", loading: "正在生成", ready: "生成完成", error: "生成失败" }[status]

  async function generate() {
    setStatus("loading")
    try {
      const fingerprint = await fingerprintPromise
      const visitor = await fingerprint.get()
      const response = await fetch(`/randomData/getOne/${visitor.visitorId}`, { headers: authHeaders() })
      if (redirectWhenUnauthorized(response)) return
      if (!response.ok) throw new Error("request failed")
      const result: ApiResult<RandomData> = await response.json()
      if (result.status !== 100) throw new Error(result.message || "request failed")
      setData(result.data)
      setStatus("ready")
      toast.success("新数据已生成")
    } catch {
      setStatus("error")
      toast.error("生成失败，请检查服务状态")
    }
  }

  async function recognize() {
    if (!imageBase64.trim()) {
      toast.error("请先输入 Base64 图片内容")
      return
    }
    setRecognizing(true)
    try {
      const response = await fetch("/randomData/getImageCode", {
        method: "POST",
        headers: { "Content-Type": "application/json", ...authHeaders() },
        body: JSON.stringify({ base64Image: imageBase64 }),
      })
      if (redirectWhenUnauthorized(response)) return
      if (!response.ok) throw new Error("request failed")
      const result: ApiResult<string> = await response.json()
      setImageCode(result.data)
      toast.success("识别完成")
    } catch {
      toast.error("识别失败，请检查图片内容")
    } finally {
      setRecognizing(false)
    }
  }

  return (
    <div className="min-h-screen bg-background">
      <header className="border-b">
        <div className="mx-auto flex h-12 max-w-7xl items-center justify-between px-5">
          <div className="flex items-center gap-3">
            <div className="grid size-8 place-items-center rounded-md bg-primary text-sm font-bold text-primary-foreground">R</div>
            <div className="leading-tight">
              <div className="text-sm font-semibold">Random Data</div>
              <div className="text-[11px] text-muted-foreground">测试数据生成器</div>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Badge variant="outline" className="gap-1.5 font-normal"><span className="size-1.5 rounded-full bg-foreground" />服务运行中</Badge>
            <Button variant="ghost" size="icon" aria-label="退出登录" title="退出登录" onClick={() => { clearToken(); window.location.assign("/") }} className="size-8 text-muted-foreground"><LogOut className="size-4" /></Button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-5 py-2">
        <section className="mb-3 flex items-end justify-between gap-6">
          <div>
            <div className="mb-1.5 flex items-center gap-2"><Badge variant="outline">Synthetic data</Badge><span className="text-xs text-muted-foreground">12 个关联字段</span></div>
            <h1 className="text-2xl font-semibold tracking-[-0.035em] sm:text-3xl">简单、可信的测试数据。</h1>
            <p className="mt-1 text-sm text-muted-foreground">一次生成完整的人员、企业与产权信息，用于开发联调和场景验证。</p>
          </div>
          <div className="hidden items-center gap-2 text-xs text-muted-foreground md:flex">
            <Sparkles className="size-3.5 text-foreground" />生成内容不对应真实个人或业务凭证
          </div>
        </section>

        <section className="space-y-3">
          <Card>
            <CardHeader className="flex-row items-center justify-between space-y-0 border-b p-3">
              <div className="flex gap-3">
                <div className="grid size-8 place-items-center rounded-md border"><Building2 className="size-4" /></div>
                <div><CardTitle className="text-sm">企业档案</CardTitle><CardDescription className="mt-0.5 text-xs">人员、企业和产权信息</CardDescription></div>
              </div>
              <Badge variant="outline" className="gap-1.5 font-normal">
                <span className={`size-1.5 rounded-full ${status === "error" ? "bg-red-500" : status === "loading" ? "animate-pulse bg-foreground" : status === "ready" ? "bg-emerald-500" : "bg-muted-foreground"}`} />
                {statusText}
              </Badge>
            </CardHeader>
            <CardContent className="p-3">
              <div className="grid gap-5 lg:grid-cols-[0.8fr_1.55fr_1fr] lg:gap-0">
              {groups.map((group, groupIndex) => (
                <div key={group.title} className={groupIndex === 1 ? "min-w-0 lg:border-x lg:px-5" : groupIndex === 2 ? "min-w-0 lg:pl-5" : "min-w-0 lg:pr-5"}>
                  <div className="mb-3 flex items-center gap-2 text-xs font-semibold"><span className="grid size-5 place-items-center rounded bg-muted text-[10px]">0{groupIndex + 1}</span>{group.title}</div>
                  <div className={groupIndex === 1 ? "grid gap-x-3 gap-y-2.5 sm:grid-cols-2" : "grid gap-2.5"}>
                    {group.fields.map(field => <DataField key={field.key} id={field.key} label={field.label} value={data[field.key]} wide={"wide" in field ? field.wide : false} />)}
                  </div>
                </div>
              ))}
              </div>
              <div className="mt-4 flex flex-col items-center gap-3 border-t pt-4 sm:flex-row">
              <Button className="h-9 w-full sm:w-64" onClick={generate} disabled={status === "loading"}>
                {status === "loading" ? <LoaderCircle className="size-4 animate-spin" /> : <RefreshCw className="size-4" />}
                {status === "loading" ? "正在生成" : "生成一组新数据"}
              </Button>
              <span className="text-[11px] text-muted-foreground">点击字段右侧图标可快速复制</span>
              </div>
            </CardContent>
          </Card>

          <details className="group rounded-lg border bg-card">
            <summary className="flex h-11 cursor-pointer list-none items-center justify-between px-4 text-sm font-medium">
              <span className="flex items-center gap-2"><FileScan className="size-4" />验证码识别 <span className="text-xs font-normal text-muted-foreground">输入 Base64 图片内容</span></span>
              <ChevronDown className="size-4 text-muted-foreground transition-transform group-open:rotate-180" />
            </summary>
            <div className="space-y-3 border-t p-4">
              <div className="flex flex-col gap-3 sm:flex-row">
                <Input className="h-9" value={imageBase64} onChange={event => setImageBase64(event.target.value)} placeholder="粘贴 Base64 图片内容" />
                <Button variant="outline" onClick={recognize} disabled={recognizing} className="sm:w-28">
                  {recognizing && <LoaderCircle className="size-4 animate-spin" />}{recognizing ? "识别中" : "开始识别"}
                </Button>
              </div>
              <Input className="h-9" readOnly value={imageCode} placeholder="识别结果将显示在这里" />
            </div>
          </details>
        </section>
      </main>
    </div>
  )
}

function App() {
  const pathname = window.location.pathname
  if (pathname === "/" || pathname === "/login") return <LoginPage />
  if (!window.localStorage.getItem(TOKEN_STORAGE_KEY)) {
    window.location.replace("/")
    return null
  }
  return <DataWorkspace />
}

export default App
