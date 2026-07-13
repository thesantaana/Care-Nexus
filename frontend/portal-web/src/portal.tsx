import { useEffect, useState, type ChangeEvent, type FormEvent, type ReactNode } from 'react';
import {
  ArrowLeft,
  ArrowRight,
  BookOpen,
  Camera,
  ChartNoAxesColumnIncreasing,
  CheckCircle2,
  CircleAlert,
  LayoutDashboard,
  LogOut,
  NotebookPen,
  Plus,
  Search,
  ShieldCheck,
  UserRound,
} from 'lucide-react';

type PortalRoute = 'login' | 'workspace';
type LoginRole = 'ADMIN' | 'CAREGIVER';

type CurrentUser = {
  userId: number;
  username: string;
  displayName: string;
  avatarUrl: string;
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
  coverUrl: string;
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
    return '集中浏览护理培训资料，掌握学习进度并参加护理知识考核。';
  }
  return '统一维护培训资源、分类标签、题库考核和AI辅助内容。';
}

function roleIcon(roleCode: string) {
  if (roleCode === 'CAREGIVER') return <BookOpen aria-hidden="true" />;
  return <ShieldCheck aria-hidden="true" />;
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
  const [selectedRole, setSelectedRole] = useState<LoginRole>('ADMIN');
  const [username, setUsername] = useState('admin_demo');
  const [password, setPassword] = useState('');
  const [pending, setPending] = useState(false);
  const [error, setError] = useState('');

  const roleOptions: Array<{
    code: LoginRole;
    title: string;
    description: string;
    demoAccount: string;
    icon: typeof ShieldCheck;
  }> = [
    {
      code: 'ADMIN',
      title: '我是管理员',
      description: '管理培训资源、分类标签、题库与考核内容',
      demoAccount: 'admin_demo',
      icon: ShieldCheck,
    },
    {
      code: 'CAREGIVER',
      title: '我是护工',
      description: '学习护理课程、参加考核并查看学习进度',
      demoAccount: 'caregiver_demo',
      icon: BookOpen,
    },
  ];

  function selectRole(role: LoginRole, demoAccount: string) {
    setSelectedRole(role);
    setUsername(demoAccount);
    setError('');
  }

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
      if (currentUser.mainRoleCode !== selectedRole) {
        throw new Error(`该账号不是${selectedRole === 'ADMIN' ? '管理员' : '护工'}账号，请重新选择身份。`);
      }
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
            <h1 className="mt-5 max-w-lg text-5xl font-semibold leading-[1.05] tracking-[-0.05em]">选择你的身份，进入对应工作台。</h1>
          </div>
          <p className="relative max-w-md text-sm leading-7 text-slate-200">管理员维护专业培训内容，护工随时学习、考核并获得AI辅助，让护理知识真正落实到日常工作。</p>
        </section>

        <section className="flex flex-col p-7 sm:p-12">
          <button className="mb-12 inline-flex items-center gap-2 self-start text-sm text-slate-600 lg:hidden" type="button" onClick={onHome}>
            <ArrowLeft size={17} aria-hidden="true" /> 返回首页
          </button>
          <div className="mb-10">
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-teal-700">Welcome back</p>
            <h2 className="mt-3 text-4xl font-semibold tracking-[-0.05em] text-slate-950">选择登录身份</h2>
            <p className="mt-3 text-sm leading-6 text-slate-500">系统会根据所选身份进入对应的管理或学习空间。</p>
          </div>

          <form className="grid gap-5" onSubmit={handleSubmit}>
            <fieldset className="grid gap-3">
              <legend className="mb-1 text-sm font-medium text-slate-700">你的身份</legend>
              <div className="grid gap-3 sm:grid-cols-2">
                {roleOptions.map((option) => {
                  const Icon = option.icon;
                  const active = selectedRole === option.code;
                  return (
                    <button
                      key={option.code}
                      className={`min-h-28 rounded-xl border p-4 text-left transition ${active ? 'border-teal-700 bg-teal-50 ring-2 ring-teal-100' : 'border-slate-200 bg-white hover:border-slate-400'}`}
                      type="button"
                      aria-pressed={active}
                      onClick={() => selectRole(option.code, option.demoAccount)}
                    >
                      <span className="flex items-center gap-2 font-semibold text-slate-900"><Icon size={19} aria-hidden="true" />{option.title}</span>
                      <span className="mt-2 block text-xs leading-5 text-slate-500">{option.description}</span>
                    </button>
                  );
                })}
              </div>
            </fieldset>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              账号
              <input className="min-h-12 rounded-xl border border-slate-300 px-4 outline-none transition focus:border-teal-600 focus:ring-4 focus:ring-teal-100" value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" placeholder={selectedRole === 'ADMIN' ? '管理员账号' : '护工账号'} required />
            </label>
            <label className="grid gap-2 text-sm font-medium text-slate-700">
              密码
              <input className="min-h-12 rounded-xl border border-slate-300 px-4 outline-none transition focus:border-teal-600 focus:ring-4 focus:ring-teal-100" value={password} onChange={(event) => setPassword(event.target.value)} type="password" autoComplete="current-password" required />
            </label>
            {error && <p className="flex gap-2 rounded-xl border border-red-200 bg-red-50 p-3 text-sm text-red-700" role="alert"><CircleAlert size={18} aria-hidden="true" />{error}</p>}
            <button className="mt-2 inline-flex min-h-12 items-center justify-center gap-2 rounded-xl bg-teal-700 px-5 font-semibold text-white transition hover:bg-teal-800 disabled:cursor-not-allowed disabled:opacity-60" type="submit" disabled={pending}>
              {pending ? '正在验证…' : `进入${selectedRole === 'ADMIN' ? '管理' : '护工'}工作台`} <ArrowRight size={18} aria-hidden="true" />
            </button>
          </form>
          <p className="mt-auto pt-10 text-xs leading-5 text-slate-400">CareNexus 使用加密认证保护账号安全。</p>
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
        description: '学习护理知识、查看个人学习进度并参加护理知识考核。',
        icon: <BookOpen aria-hidden="true" />,
      }
    : {
          eyebrow: 'MANAGEMENT PLATFORM',
          title: `${user.mainRoleName}工作区`,
          description: roleSummary(user),
          icon: roleIcon(user.mainRoleCode),
        };

  if (isCaregiver) {
    return <CaregiverLearningWorkspace user={user} resources={resources} loading={loading} resourceError={resourceError} onLogout={onLogout} />;
  }

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

        {isCaregiver && <section className="mt-10" aria-labelledby="resource-title"><div className="mb-4 flex items-end justify-between"><div><p className="text-xs font-semibold uppercase tracking-[0.18em] text-teal-700">TRAINING</p><h2 id="resource-title" className="mt-1 text-2xl font-semibold tracking-[-0.04em]">我的护理培训</h2></div></div>{loading && <p className="rounded-2xl border border-slate-200 bg-white p-6 text-sm text-slate-500">正在读取培训资源…</p>}{resourceError && <p className="rounded-2xl border border-red-200 bg-red-50 p-5 text-sm text-red-700" role="alert">{resourceError}</p>}{!loading && !resourceError && <div className="grid gap-4 md:grid-cols-3">{resources.map((resource) => <article className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm" key={resource.id}><span className="text-xs font-semibold text-teal-700">{resource.categoryName || '护理培训'} · {resource.resourceType}</span><h3 className="mt-3 font-semibold">{resource.title}</h3><p className="mt-2 line-clamp-3 text-sm leading-6 text-slate-500">{resource.summary || '查看培训资源详情与学习内容。'}</p></article>)}{resources.length === 0 && <p className="rounded-2xl border border-slate-200 bg-white p-5 text-sm text-slate-500">暂无已发布培训资源。</p>}</div>}</section>}

        {!isCaregiver && <section className="mt-10 rounded-2xl border border-slate-200 bg-white p-7"><div className="flex items-start gap-4"><span className="grid h-11 w-11 place-items-center rounded-xl bg-teal-50 text-teal-700"><ShieldCheck aria-hidden="true" /></span><div><h2 className="font-semibold">培训管理中心</h2><p className="mt-2 text-sm leading-6 text-slate-500">维护培训资料、分类标签和考核内容，为护工提供统一、可靠的学习资源。</p></div></div></section>}
      </div>
    </main>
  );
}

type LearningLibrary = {
  favorites: number[];
  completed: number[];
  notes: Record<string, string>;
};

type ProfileCustomization = {
  displayName: string;
  avatarDataUrl: string;
};

function CaregiverLearningWorkspace({ user, resources, loading, resourceError, onLogout }: {
  user: CurrentUser;
  resources: TrainingResource[];
  loading: boolean;
  resourceError: string;
  onLogout: () => void;
}) {
  const storageKey = `carenexus-portal-learning:${user.userId}`;
  const profileKey = `carenexus-portal-profile:${user.userId}`;
  const [activeSection, setActiveSection] = useState<'courses' | 'progress' | 'notes' | 'profile'>('courses');
  const [activeTab, setActiveTab] = useState<'mine' | 'all' | 'completed'>('all');
  const [keyword, setKeyword] = useState('');
  const [editingResource, setEditingResource] = useState<TrainingResource | null>(null);
  const [noteDraft, setNoteDraft] = useState('');
  const [profileMessage, setProfileMessage] = useState('');
  const [profile, setProfile] = useState<ProfileCustomization>(() => {
    try {
      return { displayName: user.displayName, avatarDataUrl: user.avatarUrl || '/assets/default-avatar.png', ...JSON.parse(localStorage.getItem(profileKey) || '{}') };
    } catch {
      return { displayName: user.displayName, avatarDataUrl: user.avatarUrl || '/assets/default-avatar.png' };
    }
  });
  const [library, setLibrary] = useState<LearningLibrary>(() => {
    try {
      return { favorites: [], completed: [], notes: {}, ...JSON.parse(localStorage.getItem(storageKey) || '{}') };
    } catch {
      return { favorites: [], completed: [], notes: {} };
    }
  });

  function updateLibrary(next: LearningLibrary) {
    setLibrary(next);
    localStorage.setItem(storageKey, JSON.stringify(next));
  }

  function saveProfile(next: ProfileCustomization) {
    const normalized = { ...next, displayName: next.displayName.trim() || user.displayName };
    setProfile(normalized);
    localStorage.setItem(profileKey, JSON.stringify(normalized));
    setProfileMessage('个人资料已保存。');
    window.setTimeout(() => setProfileMessage(''), 2200);
  }

  function handleAvatarChange(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    if (!file) return;
    if (!file.type.startsWith('image/') || file.size > 2 * 1024 * 1024) {
      setProfileMessage('请选择不超过 2 MB 的图片文件。');
      return;
    }
    const reader = new FileReader();
    reader.onload = () => saveProfile({ ...profile, avatarDataUrl: String(reader.result || '') });
    reader.readAsDataURL(file);
  }

  function toggleList(name: 'favorites' | 'completed', id: number) {
    const current = library[name];
    updateLibrary({ ...library, [name]: current.includes(id) ? current.filter((item) => item !== id) : [...current, id] });
  }

  function openNote(resource: TrainingResource) {
    setEditingResource(resource);
    setNoteDraft(library.notes[String(resource.id)] || '');
  }

  function saveCurrentNote() {
    if (!editingResource) return;
    const notes = { ...library.notes };
    if (noteDraft.trim()) notes[String(editingResource.id)] = noteDraft.trim();
    else delete notes[String(editingResource.id)];
    updateLibrary({ ...library, notes });
    setEditingResource(null);
  }

  const visibleResources = resources.filter((resource) => {
    const matchesKeyword = !keyword.trim() || `${resource.title} ${resource.summary || ''}`.toLowerCase().includes(keyword.trim().toLowerCase());
    if (!matchesKeyword) return false;
    if (activeTab === 'mine') return library.favorites.includes(resource.id);
    if (activeTab === 'completed') return library.completed.includes(resource.id);
    return true;
  });

  const navigation = [
    { id: 'courses' as const, label: '培训课程', icon: BookOpen },
    { id: 'progress' as const, label: '学习进度', icon: ChartNoAxesColumnIncreasing },
    { id: 'notes' as const, label: '学习笔记', icon: NotebookPen },
    { id: 'profile' as const, label: '我的账号', icon: UserRound },
  ];

  return (
    <main className="min-h-screen bg-[#f3f7f7] pl-[76px] text-slate-950 md:pl-[232px]">
      <aside className="fixed inset-y-0 left-0 z-30 flex w-[76px] flex-col bg-[#103f43] px-2 py-5 text-white shadow-xl md:w-[232px] md:px-5 md:py-7">
        <div className="flex items-center justify-center gap-3 md:justify-start"><span className="grid h-10 w-10 place-items-center rounded-lg bg-[#c9f5e9] text-[#103f43]"><CheckCircle2 size={20} /></span><span className="hidden md:block"><strong className="block">CareNexus</strong><small className="text-xs text-teal-200/70">护理学习平台</small></span></div>
        <div className="my-7 flex flex-col items-center rounded-lg border border-white/10 bg-white/[0.06] px-2 py-4">{profile.avatarDataUrl ? <img className="h-11 w-11 rounded-full object-cover" src={profile.avatarDataUrl} alt="当前头像" /> : <span className="grid h-11 w-11 place-items-center rounded-full bg-[#dff7f2] font-bold text-teal-800">{profile.displayName.slice(0, 1)}</span>}<strong className="mt-2 hidden text-sm md:block">{profile.displayName}</strong><small className="mt-1 hidden text-xs text-teal-200/70 md:block">护工 / 护理人员</small></div>
        <nav className="grid gap-1" aria-label="护工培训导航">{navigation.map((item) => <button key={item.id} className={`flex min-h-12 items-center justify-center gap-3 rounded-md px-3 text-sm font-medium transition md:justify-start ${activeSection === item.id ? 'bg-teal-600 text-white' : 'text-teal-100/70 hover:bg-white/10 hover:text-white'}`} type="button" onClick={() => setActiveSection(item.id)}><item.icon size={20} /><span className="hidden md:inline">{item.label}</span></button>)}</nav>
        <button className="mt-auto flex min-h-11 items-center justify-center gap-2 rounded-md text-sm text-teal-100/70 hover:bg-white/10 hover:text-white md:justify-start md:px-3" type="button" onClick={onLogout}><LogOut size={18} /><span className="hidden md:inline">退出登录</span></button>
      </aside>

      <div className="mx-auto max-w-[1500px] px-5 py-8 sm:px-8 lg:px-12">
        {activeSection === 'courses' && <>
          <header className="flex flex-col justify-between gap-5 lg:flex-row lg:items-end"><div><p className="text-xs font-bold uppercase tracking-[0.18em] text-teal-700">Course library</p><h1 className="mt-2 text-3xl font-semibold tracking-[-0.04em]">护工培训课程</h1><p className="mt-2 text-sm text-slate-500">学习护理知识，把需要学习的内容加入个人课程并记录笔记。</p></div><div className="flex gap-5 rounded-lg border border-slate-200 bg-white px-5 py-3 text-sm"><span><strong className="mr-1 text-xl text-teal-700">{resources.length}</strong>门课程</span><span><strong className="mr-1 text-xl text-teal-700">{library.favorites.length}</strong>门已加入</span></div></header>
          <div className="mt-8 flex flex-col-reverse gap-4 border-b border-slate-200 lg:flex-row lg:items-center lg:justify-between"><div className="flex gap-7 overflow-x-auto">{([{ id: 'mine', label: '我的课程' }, { id: 'all', label: '全部课程' }, { id: 'completed', label: '已完成' }] as const).map((tab) => <button key={tab.id} className={`relative min-h-12 shrink-0 text-sm font-semibold ${activeTab === tab.id ? 'text-teal-700 after:absolute after:inset-x-0 after:bottom-0 after:h-[3px] after:rounded-t after:bg-teal-600' : 'text-slate-500'}`} type="button" onClick={() => setActiveTab(tab.id)}>{tab.label}</button>)}</div><label className="relative mb-3 block w-full lg:w-72"><Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} /><input className="min-h-11 w-full rounded-full border border-slate-300 bg-white pl-10 pr-4 text-sm outline-none focus:border-teal-600 focus:ring-4 focus:ring-teal-100" value={keyword} onChange={(event) => setKeyword(event.target.value)} placeholder="搜索课程" /></label></div>
          <div className="mb-4 mt-8 flex items-end justify-between"><div><h2 className="text-xl font-semibold">{activeTab === 'all' ? '全部课程' : activeTab === 'mine' ? '我的课程' : '已完成课程'}</h2><p className="mt-1 text-xs text-slate-500">{activeTab === 'mine' ? '查看你收藏的课程' : activeTab === 'completed' ? '回顾已经完成的培训内容' : '当前已发布的护理培训内容'}</p></div><span className="text-xs text-slate-500">显示 {visibleResources.length} 项</span></div>
          {loading && <p className="rounded-lg border border-slate-200 bg-white p-6 text-sm text-slate-500">正在加载培训课程…</p>}
          {resourceError && <p className="rounded-lg border border-red-200 bg-red-50 p-5 text-sm text-red-700">{resourceError}</p>}
          {!loading && !resourceError && visibleResources.length > 0 && <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">{visibleResources.map((resource) => {
            const favorite = library.favorites.includes(resource.id);
            const completed = library.completed.includes(resource.id);
            return <article className="group relative overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm transition hover:-translate-y-1 hover:shadow-xl" key={resource.id}><div className="relative flex h-40 flex-col justify-between overflow-hidden bg-cover bg-center p-5 text-white" style={{ backgroundImage: `linear-gradient(145deg, rgba(8,47,45,.86), rgba(15,118,110,.55)), url('${resource.coverUrl || '/assets/default-course-cover.png'}')` }}><span className="relative z-10 text-xs font-semibold">{resource.resourceType === 'VIDEO' ? '视频课程' : resource.resourceType === 'PPT' ? 'PPT课程' : '文章课程'}</span><strong className="relative z-10 max-w-[12ch] text-xl">{resource.categoryName || '护理培训'}</strong><small className="relative z-10 self-end text-white/70">CareNexus</small></div><button className={`absolute right-3 top-3 z-20 grid h-9 w-9 place-items-center rounded-full border border-white/30 backdrop-blur ${favorite ? 'bg-teal-600 text-white' : 'bg-slate-900/25 text-white'}`} type="button" aria-label={favorite ? '移出我的课程' : '加入我的课程'} onClick={() => toggleList('favorites', resource.id)}><Plus className={`transition ${favorite ? 'rotate-45' : ''}`} size={19} /></button><div className="p-5"><h3 className="min-h-12 font-semibold leading-6">{resource.title}</h3><p className="mt-2 line-clamp-2 min-h-10 text-xs leading-5 text-slate-500">{resource.summary || '进入课程查看完整培训内容。'}</p><div className="mt-4 flex items-center justify-between border-t border-slate-100 pt-4"><span className={`text-xs ${completed ? 'font-semibold text-emerald-700' : 'text-slate-400'}`}>{completed ? '已完成' : favorite ? '已加入' : '未加入'}</span><div className="flex gap-3"><button className="text-xs font-semibold text-slate-500 hover:text-teal-700" type="button" onClick={() => openNote(resource)}>记笔记</button><button className="text-xs font-semibold text-teal-700" type="button" onClick={() => toggleList('completed', resource.id)}>{completed ? '重新学习' : '标记完成'}</button></div></div></div></article>;
          })}</div>}
          {!loading && !resourceError && visibleResources.length === 0 && <div className="rounded-lg border border-dashed border-slate-300 bg-white p-10 text-center"><BookOpen className="mx-auto text-teal-600" /><h3 className="mt-4 font-semibold">暂无对应课程</h3><p className="mt-2 text-sm text-slate-500">可以切换到全部课程继续浏览。</p></div>}
        </>}

        {activeSection === 'progress' && <SimpleLearningSection icon={<ChartNoAxesColumnIncreasing />} title="学习进度" description="集中查看当前账号的课程完成情况。"><div className="grid gap-4 sm:grid-cols-3"><Metric label="课程总数" value={resources.length} /><Metric label="已加入课程" value={library.favorites.length} /><Metric label="已完成" value={library.completed.length} /></div></SimpleLearningSection>}
        {activeSection === 'notes' && <SimpleLearningSection icon={<NotebookPen />} title="学习笔记" description="笔记仅保存在当前设备和账号下。"><div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">{Object.entries(library.notes).map(([id, note]) => { const resource = resources.find((item) => item.id === Number(id)); return <article className="rounded-lg border border-slate-200 bg-white p-5" key={id}><small className="text-teal-700">课程笔记</small><h3 className="mt-2 font-semibold">{resource?.title || `培训课程 #${id}`}</h3><p className="mt-3 whitespace-pre-wrap text-sm leading-6 text-slate-500">{note}</p>{resource && <button className="mt-4 text-xs font-semibold text-teal-700" type="button" onClick={() => openNote(resource)}>编辑笔记</button>}</article>; })}{Object.keys(library.notes).length === 0 && <p className="rounded-lg border border-dashed border-slate-300 bg-white p-8 text-sm text-slate-500">还没有笔记，请从课程卡片点击“记笔记”。</p>}</div></SimpleLearningSection>}
        {activeSection === 'profile' && <SimpleLearningSection icon={<UserRound />} title="我的账号" description="维护当前设备上显示的头像和姓名。"><div className="max-w-2xl rounded-lg border border-slate-200 bg-white p-6 sm:p-8"><div className="flex flex-col gap-7 sm:flex-row"><div className="relative h-24 w-24 shrink-0">{profile.avatarDataUrl ? <img className="h-24 w-24 rounded-full object-cover" src={profile.avatarDataUrl} alt="个人头像" /> : <span className="grid h-24 w-24 place-items-center rounded-full bg-teal-100 text-3xl font-bold text-teal-800">{profile.displayName.slice(0, 1)}</span>}<label className="absolute bottom-0 right-0 grid h-9 w-9 cursor-pointer place-items-center rounded-full bg-teal-700 text-white shadow-lg" title="更换头像"><Camera size={17} /><input className="sr-only" type="file" accept="image/*" onChange={handleAvatarChange} /></label></div><div className="grid flex-1 gap-5"><label className="grid gap-2 text-sm font-medium text-slate-700">显示姓名<input className="min-h-11 rounded-md border border-slate-300 px-3 outline-none focus:border-teal-600 focus:ring-4 focus:ring-teal-100" value={profile.displayName} maxLength={30} onChange={(event) => setProfile({ ...profile, displayName: event.target.value })} /></label><div className="grid gap-1 text-sm"><span className="text-slate-500">登录账号</span><strong>{user.username}</strong></div><div className="grid gap-1 text-sm"><span className="text-slate-500">身份</span><strong>{user.mainRoleName}</strong></div><button className="min-h-11 justify-self-start rounded-md bg-teal-700 px-5 text-sm font-semibold text-white" type="button" onClick={() => saveProfile(profile)}>保存个人资料</button>{profileMessage && <p className="text-sm text-teal-700" role="status">{profileMessage}</p>}</div></div></div></SimpleLearningSection>}
      </div>

      {editingResource && <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/45 p-5" role="dialog" aria-modal="true" aria-labelledby="note-dialog-title"><div className="w-full max-w-xl rounded-lg bg-white p-6 shadow-2xl"><p className="text-xs font-bold uppercase tracking-[0.16em] text-teal-700">Course note</p><h2 className="mt-2 text-xl font-semibold" id="note-dialog-title">{editingResource.title}</h2><textarea className="mt-5 min-h-40 w-full resize-y rounded-lg border border-slate-300 p-4 text-sm leading-6 outline-none focus:border-teal-600 focus:ring-4 focus:ring-teal-100" value={noteDraft} onChange={(event) => setNoteDraft(event.target.value)} placeholder="记录课程重点、操作要点或需要复习的内容…" maxLength={2000} /><div className="mt-4 flex justify-end gap-3"><button className="min-h-10 rounded-md px-4 text-sm text-slate-600" type="button" onClick={() => setEditingResource(null)}>取消</button><button className="min-h-10 rounded-md bg-teal-700 px-5 text-sm font-semibold text-white" type="button" onClick={saveCurrentNote}>保存笔记</button></div></div></div>}
    </main>
  );
}

function SimpleLearningSection({ icon, title, description, children }: { icon: ReactNode; title: string; description: string; children: ReactNode }) {
  return <section><header className="mb-8 flex items-center gap-4"><span className="grid h-12 w-12 place-items-center rounded-lg bg-teal-100 text-teal-700">{icon}</span><div><h1 className="text-3xl font-semibold tracking-[-0.04em]">{title}</h1><p className="mt-1 text-sm text-slate-500">{description}</p></div></header>{children}</section>;
}

function Metric({ label, value }: { label: string; value: number }) {
  return <article className="rounded-lg border border-slate-200 bg-white p-6"><LayoutDashboard className="text-teal-700" size={21} /><strong className="mt-5 block text-3xl">{value}</strong><span className="mt-1 block text-sm text-slate-500">{label}</span></article>;
}
