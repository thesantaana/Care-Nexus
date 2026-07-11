import { useEffect, useState, type FormEvent } from 'react';
import {
  ArrowLeft,
  ArrowRight,
  BookOpen,
  CalendarClock,
  CheckCircle2,
  CircleAlert,
  LogOut,
  ShieldCheck,
  Stethoscope,
  UsersRound,
} from 'lucide-react';

type PortalRoute = 'login' | 'workspace';

type CurrentUser = {
  userId: number;
  username: string;
  displayName: string;
  mainRoleCode: string;
  mainRoleName: string;
  permissionCodes: string[];
  accountStatus: string;
};

type LoginResponse = CurrentUser & {
  token: string;
};

type TrainingResource = {
  id: number;
  title: string;
  summary: string;
  resourceType: string;
  categoryName: string;
  status: string;
};

type ApiResponse<T> = {
  code: string;
  message: string;
  data: T;
};

const TOKEN_KEY = 'carenexus-react-token';
const USER_KEY = 'carenexus-react-user';

function hasTrainingAccess(user: CurrentUser) {
  return user.permissionCodes.includes('training:resource:view') ||
    user.permissionCodes.includes('training:resource:manage');
}

async function api<T>(path: string, options: RequestInit = {}, token?: string): Promise<T> {
  const headers = new Headers(options.headers);
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }
  if (options.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  const response = await fetch(`/api/v1${path}`, { ...options, headers });
  const payload = await response.json() as ApiResponse<T>;
  if (!response.ok || payload.code !== 'SUCCESS') {
    throw new Error(payload.message || '请求失败，请稍后重试。');
  }
  return payload.data;
}

function saveSession(token: string, user: CurrentUser) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

function roleSummary(user: CurrentUser) {
  if (user.mainRoleCode === 'CAREGIVER') {
    return '从培训学习到护理订单执行，个人进度与服务安排会在这里汇合。';
  }
  if (user.mainRoleCode === 'DOCTOR' || user.mainRoleCode === 'HEALTH_MANAGER') {
    return '健康档案、风险预警和随访工作将围绕已授权老人逐步接入。';
  }
  if (user.mainRoleCode === 'ADMIN' || user.mainRoleCode === 'OPERATOR') {
    return '统一管理账号、服务流程与运营协同，业务权限以服务端返回结果为准。';
  }
  return '从培训资源管理开始，逐步完成内容、学习与服务协同。';
}

function roleIcon(roleCode: string) {
  if (roleCode === 'CAREGIVER') return <BookOpen aria-hidden="true" />;
  if (roleCode === 'DOCTOR' || roleCode === 'HEALTH_MANAGER') return <Stethoscope aria-hidden="true" />;
  if (roleCode === 'ADMIN' || roleCode === 'OPERATOR') return <ShieldCheck aria-hidden="true" />;
  return <UsersRound aria-hidden="true" />;
}

export function Portal({ route, onHome, onRouteChange }: {
  route: PortalRoute;
  onHome: () => void;
  onRouteChange: (route: PortalRoute) => void;
}) {
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [checking, setChecking] = useState(true);

  useEffect(() => {
    const savedToken = localStorage.getItem(TOKEN_KEY);
    if (!savedToken) {
      setChecking(false);
      return;
    }

    api<CurrentUser>('/auth/me', {}, savedToken)
      .then((currentUser) => {
        setToken(savedToken);
        setUser(currentUser);
        saveSession(savedToken, currentUser);
      })
      .catch(() => clearSession())
      .finally(() => setChecking(false));
  }, []);

  function handleSignedIn(login: LoginResponse, currentUser: CurrentUser) {
    setToken(login.token);
    setUser(currentUser);
    saveSession(login.token, currentUser);
    onRouteChange('workspace');
  }

  async function handleLogout() {
    if (token) {
      try {
        await api('/auth/logout', { method: 'POST' }, token);
      } catch {
        // 本地状态仍须在网络异常时清理。
      }
    }
    clearSession();
    setToken(null);
    setUser(null);
    onHome();
  }

  if (checking) {
    return <main className="grid min-h-screen place-items-center bg-slate-950 text-sm text-slate-200">正在恢复登录状态…</main>;
  }

  if (route === 'workspace' && user && token) {
    return <Workspace user={user} token={token} onHome={onHome} onLogout={handleLogout} />;
  }

  return <Login onHome={onHome} onSignedIn={handleSignedIn} />;
}

function Login({ onHome, onSignedIn }: {
  onHome: () => void;
  onSignedIn: (login: LoginResponse, user: CurrentUser) => void;
}) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [pending, setPending] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    setPending(true);
    try {
      const login = await api<LoginResponse>('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username: username.trim(), password }),
      });
      const currentUser = await api<CurrentUser>('/auth/me', {}, login.token);
      onSignedIn(login, currentUser);
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : '登录失败，请稍后重试。');
    } finally {
      setPending(false);
    }
  }

  return (
    <main className="min-h-screen bg-[#edf4f6] px-5 py-5 text-slate-950 sm:p-8">
      <div className="mx-auto grid min-h-[calc(100vh-40px)] max-w-6xl overflow-hidden rounded-[2rem] border border-white/80 bg-white shadow-[0_32px_100px_rgba(15,23,42,0.16)] lg:grid-cols-[1.08fr_0.92fr]">
        <section className="relative hidden overflow-hidden bg-slate-950 p-12 text-white lg:flex lg:flex-col lg:justify-between">
          <div className="absolute inset-0 bg-cover bg-center opacity-40" style={{ backgroundImage: "url('/assets/gene_grass.png')" }} />
          <div className="absolute inset-0 bg-[linear-gradient(140deg,rgba(15,23,42,0.96),rgba(15,118,110,0.64))]" />
          <div className="relative">
            <button className="inline-flex items-center gap-2 text-sm text-slate-200 transition hover:text-white" type="button" onClick={onHome}>
              <ArrowLeft size={17} aria-hidden="true" /> 返回首页
            </button>
            <p className="mt-20 text-xs font-semibold uppercase tracking-[0.22em] text-teal-200">CareNexus unified access</p>
            <h1 className="mt-5 max-w-lg text-5xl font-semibold leading-[1.05] tracking-[-0.05em]">从同一个入口，进入不同角色的照护协作。</h1>
          </div>
          <p className="relative max-w-md text-sm leading-7 text-slate-200">护工/护理人员、培训管理、医生和管理员共用身份认证与权限边界，业务能力按后端实际开放情况呈现。</p>
        </section>

        <section className="flex flex-col p-7 sm:p-12">
          <button className="mb-12 inline-flex items-center gap-2 self-start text-sm text-slate-600 lg:hidden" type="button" onClick={onHome}>
            <ArrowLeft size={17} aria-hidden="true" /> 返回首页
          </button>
          <div className="mb-10">
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-teal-700">Welcome back</p>
            <h2 className="mt-3 text-4xl font-semibold tracking-[-0.05em] text-slate-950">登录工作台</h2>
            <p className="mt-3 text-sm leading-6 text-slate-500">使用后端配置的项目账号登录。系统会再次读取当前角色与权限。</p>
          </div>

          <form className="grid gap-5" onSubmit={handleSubmit}>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              账号
              <input className="min-h-12 rounded-xl border border-slate-300 px-4 outline-none transition focus:border-teal-600 focus:ring-4 focus:ring-teal-100" value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" required />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              密码
              <input className="min-h-12 rounded-xl border border-slate-300 px-4 outline-none transition focus:border-teal-600 focus:ring-4 focus:ring-teal-100" value={password} onChange={(event) => setPassword(event.target.value)} type="password" autoComplete="current-password" required />
            </label>
            {error && <p className="flex gap-2 rounded-xl border border-red-200 bg-red-50 p-3 text-sm text-red-700" role="alert"><CircleAlert size={18} aria-hidden="true" />{error}</p>}
            <button className="mt-2 inline-flex min-h-12 items-center justify-center gap-2 rounded-xl bg-teal-700 px-5 font-semibold text-white transition hover:bg-teal-800 disabled:cursor-not-allowed disabled:opacity-60" type="submit" disabled={pending}>
              {pending ? '正在验证…' : '进入工作台'} <ArrowRight size={18} aria-hidden="true" />
            </button>
          </form>
          <p className="mt-auto pt-10 text-xs leading-5 text-slate-400">演示账号请使用数据库种子数据中的账号；登录信息只发送至本机 CareNexus 后端。</p>
        </section>
      </div>
    </main>
  );
}

function Workspace({ user, token, onHome, onLogout }: {
  user: CurrentUser;
  token: string;
  onHome: () => void;
  onLogout: () => void;
}) {
  const [resources, setResources] = useState<TrainingResource[]>([]);
  const [resourceError, setResourceError] = useState('');
  const [loading, setLoading] = useState(hasTrainingAccess(user));
  const isCaregiver = user.mainRoleCode === 'CAREGIVER';
  const isDoctor = user.mainRoleCode === 'DOCTOR' || user.mainRoleCode === 'HEALTH_MANAGER';

  useEffect(() => {
    if (!hasTrainingAccess(user)) return;
    api<{ records: TrainingResource[] }>('/training/resources?status=PUBLISHED&pageNo=1&pageSize=3', {}, token)
      .then((page) => setResources(page.records))
      .catch((requestError) => setResourceError(requestError instanceof Error ? requestError.message : '培训资源暂时不可用。'))
      .finally(() => setLoading(false));
  }, [token, user]);

  const platform = isCaregiver
    ? {
        eyebrow: 'CAREGIVER PLATFORM',
        title: '护工 / 护理人员工作区',
        description: '学习护理知识、查看个人学习进度，并在护理订单接口开放后执行服务。',
        icon: <BookOpen aria-hidden="true" />,
      }
    : isDoctor
      ? {
          eyebrow: 'DOCTOR PLATFORM',
          title: '医生健康管理工作区',
          description: '已完成身份与权限校验；授权老人、健康预警和随访将在医生接口完成后进入此处。',
          icon: <Stethoscope aria-hidden="true" />,
        }
      : {
          eyebrow: 'MANAGEMENT PLATFORM',
          title: `${user.mainRoleName}工作区`,
          description: roleSummary(user),
          icon: roleIcon(user.mainRoleCode),
        };

  return (
    <main className="min-h-screen bg-[#f4f8f8] text-slate-950">
      <header className="sticky top-0 z-20 border-b border-slate-200/80 bg-white/85 px-5 py-4 backdrop-blur-xl sm:px-8">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-4">
          <button className="flex items-center gap-2 text-left" type="button" onClick={onHome}>
            <span className="grid h-9 w-9 place-items-center rounded-xl bg-teal-700 text-white"><CheckCircle2 size={19} aria-hidden="true" /></span>
            <span><strong className="block">Nexus</strong><small className="text-xs text-slate-500">返回公开首页</small></span>
          </button>
          <div className="flex items-center gap-3">
            <span className="hidden text-right text-sm sm:block"><strong className="block">{user.displayName}</strong><small className="text-slate-500">{user.mainRoleName}</small></span>
            <button className="inline-flex min-h-11 items-center gap-2 rounded-xl border border-slate-300 px-3 text-sm font-medium text-slate-700 transition hover:border-slate-400" type="button" onClick={onLogout}><LogOut size={17} aria-hidden="true" />退出</button>
          </div>
        </div>
      </header>

      <div className="mx-auto max-w-6xl px-5 py-10 sm:px-8 sm:py-14">
        <section className="overflow-hidden rounded-[1.75rem] bg-slate-950 p-7 text-white shadow-[0_24px_70px_rgba(15,23,42,0.18)] sm:p-10">
          <div className="flex flex-col gap-7 sm:flex-row sm:items-end sm:justify-between">
            <div className="max-w-2xl"><p className="text-xs font-semibold uppercase tracking-[0.2em] text-teal-200">{platform.eyebrow}</p><h1 className="mt-3 text-4xl font-semibold tracking-[-0.05em]">{platform.title}</h1><p className="mt-4 text-sm leading-7 text-slate-300">你好，{user.displayName}。{platform.description}</p></div>
            <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white/10 text-teal-200">{platform.icon}</div>
          </div>
        </section>

        {isCaregiver && <section className="mt-10" aria-labelledby="resource-title"><div className="mb-4 flex items-end justify-between"><div><p className="text-xs font-semibold uppercase tracking-[0.18em] text-teal-700">Live training data</p><h2 id="resource-title" className="mt-1 text-2xl font-semibold tracking-[-0.04em]">我的护理培训</h2></div><span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-semibold text-emerald-700">培训资源已接入</span></div>{loading && <p className="rounded-2xl border border-slate-200 bg-white p-6 text-sm text-slate-500">正在读取培训资源…</p>}{resourceError && <p className="rounded-2xl border border-red-200 bg-red-50 p-5 text-sm text-red-700" role="alert">{resourceError}</p>}{!loading && !resourceError && <div className="grid gap-4 md:grid-cols-3">{resources.map((resource) => <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm" key={resource.id}><span className="text-xs font-semibold text-teal-700">{resource.categoryName || '护理培训'} · {resource.resourceType}</span><h3 className="mt-3 font-semibold">{resource.title}</h3><p className="mt-2 line-clamp-3 text-sm leading-6 text-slate-500">{resource.summary || '查看培训资源详情与学习内容。'}</p></article>)}{resources.length === 0 && <p className="rounded-2xl border border-slate-200 bg-white p-5 text-sm text-slate-500">暂无已发布培训资源。</p>}</div>}<article className="mt-4 rounded-2xl border border-slate-200 bg-white p-6"><div className="flex items-start gap-4"><span className="grid h-11 w-11 place-items-center rounded-xl bg-slate-100 text-slate-600"><CalendarClock aria-hidden="true" /></span><div><h2 className="font-semibold">护理订单执行</h2><p className="mt-1 text-sm leading-6 text-slate-500">订单查询、确认、开始和完成服务会在护理订单后端接口开放后加入此工作区。</p></div></div></article></section>}

        {isDoctor && <section className="mt-10 rounded-2xl border border-slate-200 bg-white p-7"><div className="flex items-start gap-4"><span className="grid h-11 w-11 place-items-center rounded-xl bg-teal-50 text-teal-700"><Stethoscope aria-hidden="true" /></span><div><p className="text-xs font-semibold uppercase tracking-[0.18em] text-teal-700">Doctor only</p><h2 className="mt-2 text-2xl font-semibold tracking-[-0.04em]">授权老人健康管理</h2><p className="mt-3 max-w-2xl text-sm leading-7 text-slate-500">这是仅在医生或健康管理人员登录后可见的区域。当前后端尚未开放健康档案、预警、随访和评估接口，接口完成后会直接接入这里。</p></div></div></section>}

        {!isCaregiver && !isDoctor && <section className="mt-10 rounded-2xl border border-slate-200 bg-white p-7"><div className="flex items-start gap-4"><span className="grid h-11 w-11 place-items-center rounded-xl bg-slate-100 text-slate-600"><ShieldCheck aria-hidden="true" /></span><div><h2 className="font-semibold">当前角色平台正在完善</h2><p className="mt-2 text-sm leading-6 text-slate-500">该入口只对当前登录角色可见。现有后端接口完成后会在此扩展，不在公开首页展示。</p></div></div></section>}
      </div>
    </main>
  );
}
