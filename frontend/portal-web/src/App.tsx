import { useEffect, useRef, useState } from 'react';
import type { LucideIcon } from 'lucide-react';
import {
  Activity,
  ArrowLeft,
  ArrowUpRight,
  Bot,
  BrainCircuit,
  Database,
  LockKeyhole,
  Menu,
  Network,
  ShieldCheck,
  Sparkles,
  Wallet,
  Zap,
} from 'lucide-react';
import { Portal } from './portal';

const BG_IMAGE_1 = '/assets/gene.png';

const BG_IMAGE_2 = '/assets/gene_grass.png';

const VIDEO_MP4 = '/assets/bg-video.mp4';

const VIDEO_POSTER = '/assets/first-frame.jpg';

const AI_ASSISTANT_DETAIL_MP4 = '/assets/ai-assistant-detail.mp4';

const SPOTLIGHT_R = 260;

type Point = {
  x: number;
  y: number;
};

type AppRoute = 'home' | 'concept' | 'assistant' | 'login' | 'workspace';

const ROUTE_SEO: Record<AppRoute, { title: string; description: string; robots: string }> = {
  home: {
    title: 'CareNexus 颐联护理培训平台｜护工课程与 AI 学习助手',
    description: 'CareNexus 颐联护理培训平台，为护工提供护理课程、资料问答、知识总结、学习建议、练习辅助与在线考核。',
    robots: 'index,follow,max-image-preview:large,max-snippet:-1,max-video-preview:-1',
  },
  concept: {
    title: '护理培训数字化解决方案｜CareNexus 产品理念',
    description: '了解 CareNexus 如何连接护理培训资料、学习过程、作业考试和培训结果，形成可追踪的护理能力成长路径。',
    robots: 'index,follow,max-image-preview:large,max-snippet:-1',
  },
  assistant: {
    title: '护理培训 AI 学习助手｜资料问答、总结与练习辅助',
    description: 'CareNexus AI 学习助手基于已发布护理培训资料，提供资料问答、知识总结、学习建议和练习辅助。',
    robots: 'index,follow,max-image-preview:large,max-snippet:-1,max-video-preview:-1',
  },
  login: {
    title: '登录 CareNexus 护理培训平台',
    description: '登录 CareNexus 管理端或护工学习工作台。',
    robots: 'noindex,nofollow',
  },
  workspace: {
    title: 'CareNexus 护理培训工作台',
    description: 'CareNexus 用户工作台。',
    robots: 'noindex,nofollow',
  },
};

function updatePortalSeo(route: AppRoute) {
  const seo = ROUTE_SEO[route];
  document.title = seo.title;
  const values = [
    ['meta[name="description"]', 'name', 'description', seo.description],
    ['meta[name="robots"]', 'name', 'robots', seo.robots],
    ['meta[property="og:title"]', 'property', 'og:title', seo.title],
    ['meta[property="og:description"]', 'property', 'og:description', seo.description],
    ['meta[name="twitter:title"]', 'name', 'twitter:title', seo.title],
    ['meta[name="twitter:description"]', 'name', 'twitter:description', seo.description],
  ];

  values.forEach(([selector, attribute, key, content]) => {
    let element = document.head.querySelector<HTMLMetaElement>(selector);
    if (!element) {
      element = document.createElement('meta');
      element.setAttribute(attribute, key);
      document.head.appendChild(element);
    }
    element.content = content;
  });
}

type RevealLayerProps = {
  image: string;
  cursorX: number;
  cursorY: number;
};

type CryptoModule = {
  id: string;
  title: string;
  eyebrow: string;
  icon: LucideIcon;
  summary: string;
  metrics: string;
  points: string[];
  wide?: boolean;
};

type TrainingCard = {
  title: string;
  eyebrow: string;
  icon: LucideIcon;
  summary: string;
  metric: string;
  className: string;
  dark?: boolean;
};

const protocolMetrics = [
  { value: '12+', label: 'active chains', detail: 'EVM, L2 and modular settlement lanes' },
  { value: '1,000+', label: 'intent routes', detail: 'priced continuously across vaults' },
  { value: '$10b', label: 'simulated flow', detail: 'stress-tested treasury movement' },
  { value: '20ms', label: 'signal refresh', detail: 'risk and liquidity telemetry' },
];

const cryptoSignals = [
  { label: 'Intent Layer', value: 'routing live' },
  { label: 'Vault Mesh', value: 'collateral synced' },
  { label: 'Risk Guard', value: 'oracle stable' },
  { label: 'Builder API', value: 'hooks ready' },
];

const cryptoModules: CryptoModule[] = [
  {
    id: 'training-resources',
    title: '培训资源中心',
    eyebrow: '01 / LEARNING CONTENT',
    icon: Database,
    summary: '集中维护护理文章、视频和PPT，让学习资料分类清晰、持续更新。',
    metrics: '多类型内容管理',
    points: ['文章与视频', '分类标签', '发布与下架'],
    wide: true,
  },
  {
    id: 'learning-progress',
    title: '学习进度',
    eyebrow: '02 / LEARNING PROGRESS',
    icon: ShieldCheck,
    summary: '记录资源访问、整体学习时长和最近学习时间，清晰掌握培训状态。',
    metrics: '学习过程可追踪',
    points: ['访问记录', '累计时长', '培训状态'],
    wide: true,
  },
  {
    id: 'training-exam',
    title: '护理知识考核',
    eyebrow: '03 / KNOWLEDGE EXAM',
    icon: Activity,
    summary: '通过单选题和判断题检验学习成果，系统自动评分并更新培训状态。',
    metrics: '客观题自动评分',
    points: ['题库维护', '考核发布', '成绩记录'],
  },
  {
    id: 'ai-assistant',
    title: 'AI学习助手',
    eyebrow: '04 / AI ASSISTANT',
    icon: LockKeyhole,
    summary: '基于已发布培训资料提供问答、总结和学习建议，回答保留资料来源。',
    metrics: '内容有据可查',
    points: ['资料问答', '知识总结', '学习建议'],
  },
  {
    id: 'ai-question-draft',
    title: 'AI题目草稿',
    eyebrow: '05 / QUESTION DRAFT',
    icon: BrainCircuit,
    summary: '根据入库资料生成单选题和判断题草稿，经管理员审核后进入正式题库。',
    metrics: '管理员审核后入库',
    points: ['基于资料生成', '人工审核', '题库留痕'],
  },
];

const systemMetrics = protocolMetrics;

const moduleCards = cryptoModules;

const blueprintNodes = [
  { label: 'Intent AI', icon: Bot, detail: 'Signals / routing / pricing' },
  { label: 'Liquidity', icon: Activity, detail: 'Vaults / routes / execution' },
  { label: 'Settlement', icon: Zap, detail: 'Receipts / policies / finality' },
  { label: 'Security', icon: LockKeyhole, detail: 'Risk / oracles / guardrails' },
];

const trainingCards: TrainingCard[] = [
  {
    title: 'AI 情景训练',
    eyebrow: 'Scenario Lab',
    icon: BrainCircuit,
    summary: '用问答、案例和复盘模拟真实照护现场，帮助护工在练习中建立判断力。',
    metric: '跌倒 / 压疮 / 认知障碍',
    className: 'md:-translate-y-3 md:-rotate-6',
  },
  {
    title: '课程学习',
    eyebrow: 'Learning Path',
    icon: Sparkles,
    summary: '围绕老年护理理论、基础知识和实操技能生成清晰学习路径。',
    metric: '理论 / 知识 / 技能',
    className: 'md:translate-y-7 md:-rotate-2',
  },
  {
    title: '内容素材库',
    eyebrow: 'Content Hub',
    icon: Database,
    summary: '统一管理培训文章、视频、PPT、类别与标签，让课程更新更轻。',
    metric: '文章 / 视频 / PPT',
    className: 'md:-translate-y-8',
    dark: true,
  },
  {
    title: '应急处理',
    eyebrow: 'Response Drill',
    icon: ShieldCheck,
    summary: '把常见突发状况拆成可训练步骤，强化规范处置和风险意识。',
    metric: '预警 / 判断 / 上报',
    className: 'md:translate-y-6 md:rotate-3',
  },
  {
    title: '沟通关怀',
    eyebrow: 'Human Care',
    icon: Activity,
    summary: '训练职业道德、家属沟通和人文关怀，让专业服务更有温度。',
    metric: '沟通 / 责任 / 关怀',
    className: 'md:-translate-y-4 md:rotate-6',
  },
];

function useInViewOnce<T extends HTMLElement>(threshold = 0.18) {
  const ref = useRef<T | null>(null);
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    const node = ref.current;

    if (!node || isVisible) {
      return undefined;
    }

    if (!('IntersectionObserver' in window)) {
      setIsVisible(true);
      return undefined;
    }

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true);
          observer.disconnect();
        }
      },
      {
        threshold,
        rootMargin: '0px 0px -12% 0px',
      },
    );

    observer.observe(node);

    return () => {
      observer.disconnect();
    };
  }, [isVisible, threshold]);

  return [ref, isVisible] as const;
}

function RevealLayer({ image, cursorX, cursorY }: RevealLayerProps) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const revealRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) {
      return undefined;
    }

    const resizeCanvas = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    };

    resizeCanvas();
    window.addEventListener('resize', resizeCanvas);

    return () => {
      window.removeEventListener('resize', resizeCanvas);
    };
  }, []);

  useEffect(() => {
    const canvas = canvasRef.current;
    const reveal = revealRef.current;
    const context = canvas?.getContext('2d');

    if (!canvas || !reveal || !context) {
      return;
    }

    context.clearRect(0, 0, canvas.width, canvas.height);

    const gradient = context.createRadialGradient(
      cursorX,
      cursorY,
      0,
      cursorX,
      cursorY,
      SPOTLIGHT_R,
    );

    gradient.addColorStop(0, 'rgba(255,255,255,1)');
    gradient.addColorStop(0.4, 'rgba(255,255,255,1)');
    gradient.addColorStop(0.6, 'rgba(255,255,255,0.75)');
    gradient.addColorStop(0.75, 'rgba(255,255,255,0.4)');
    gradient.addColorStop(0.88, 'rgba(255,255,255,0.12)');
    gradient.addColorStop(1, 'rgba(255,255,255,0)');

    context.fillStyle = gradient;
    context.beginPath();
    context.arc(cursorX, cursorY, SPOTLIGHT_R, 0, Math.PI * 2);
    context.fill();

    const maskImage = `url(${canvas.toDataURL()})`;
    reveal.style.setProperty('mask-image', maskImage);
    reveal.style.setProperty('-webkit-mask-image', maskImage);
    reveal.style.setProperty('mask-size', '100% 100%');
    reveal.style.setProperty('-webkit-mask-size', '100% 100%');
  });

  return (
    <>
      <canvas
        ref={canvasRef}
        className="absolute inset-0 pointer-events-none"
        style={{ display: 'none' }}
      />
      <div
        ref={revealRef}
        className="absolute inset-0 bg-center bg-cover bg-no-repeat z-30 pointer-events-none"
        style={{
          backgroundImage: `url(${image})`,
          maskSize: '100% 100%',
          WebkitMaskSize: '100% 100%',
        }}
      />
    </>
  );
}

function Navigation({ onOpenPortal }: { onOpenPortal: () => void }) {
  return (
    <nav className="fixed top-0 left-0 right-0 z-[100] flex items-center justify-between p-4 sm:p-5">
      <a href="#home" className="flex items-center gap-2.5" aria-label="CareNexus 首页">
        <svg width="26" height="26" viewBox="0 0 256 256" fill="#1f2937" aria-hidden="true">
          <path d="M 256 256 L 128 256 L 0 128 L 128 128 Z M 256 128 L 128 128 L 0 0 L 128 0 Z" />
        </svg>
        <span className="text-gray-900 text-2xl font-playfair italic">CareNexus</span>
      </a>

      <button
        className="hidden md:block bg-slate-900 text-white text-sm font-semibold px-6 py-2.5 rounded-full shadow-[0_16px_40px_rgba(15,23,42,0.16)] transition-all hover:bg-slate-700 hover:shadow-[0_18px_48px_rgba(56,189,248,0.24)]"
        type="button"
        onClick={onOpenPortal}
      >
        登录
      </button>

      <button
        className="md:hidden inline-flex h-11 w-11 items-center justify-center rounded-full border border-slate-300/70 bg-white/70 text-slate-900 backdrop-blur-xl transition-colors hover:bg-white/90"
        type="button"
        aria-label="打开导航"
        onClick={onOpenPortal}
      >
        <Menu aria-hidden="true" size={21} strokeWidth={2} />
      </button>
    </nav>
  );
}

function PlatformOverview() {
  const [overviewRef, overviewVisible] = useInViewOnce<HTMLElement>(0.16);
  const visibleClass = overviewVisible ? 'is-visible' : '';

  return (
    <section
      ref={overviewRef}
      className="relative overflow-hidden bg-[#f7f9fc] px-5 pb-24 pt-32 sm:px-8 lg:px-12"
    >
      <div className="pointer-events-none absolute inset-x-0 top-0 h-44 bg-gradient-to-b from-white to-transparent" />
      <div className="relative mx-auto max-w-6xl">
        <div
          className={`mb-8 grid gap-6 lg:grid-cols-[1fr_360px] lg:items-end scroll-anim scroll-reveal ${visibleClass}`}
          style={{ animationDelay: '0.04s' }}
        >
          <div>
            <p className="mb-3 text-xs font-semibold uppercase tracking-[0.24em] text-sky-600">
              Nexus Intelligent Care
            </p>
            <h2 className="max-w-3xl text-3xl font-semibold tracking-[-0.05em] text-slate-950 sm:text-5xl">
              一张面向未来智能医院的服务蓝图
            </h2>
          </div>
          <p className="text-sm leading-7 text-slate-600">
            参考工程蓝图的结构感，将公开入口、服务流、AI 培训和后台治理拆成可识别的模块，而不是把信息堆成一整块说明。
          </p>
        </div>

        <div
          className={`relative overflow-hidden border border-slate-300/80 bg-white/80 shadow-[0_30px_110px_rgba(148,163,184,0.26)] backdrop-blur-xl overview-play ${visibleClass}`}
          style={{ animationDelay: '0.12s' }}
        >
          <div className="absolute inset-0 bg-[radial-gradient(circle_at_50%_20%,rgba(186,230,253,0.42),transparent_30%),linear-gradient(135deg,rgba(255,255,255,0.94),rgba(241,245,249,0.64))]" />
          <div className="relative z-10 flex h-12 items-center justify-between border-b border-slate-300/70 bg-white/60 px-4">
            <div className="flex items-center gap-2">
              <span className="h-2.5 w-2.5 rounded-full bg-slate-300" />
              <span className="h-2.5 w-2.5 rounded-full bg-sky-300" />
              <span className="h-2.5 w-2.5 rounded-full bg-slate-400" />
            </div>
            <p className="text-[11px] font-semibold uppercase tracking-[0.24em] text-slate-500">
              Care Operating Map
            </p>
          </div>

          <div className="relative z-10 grid gap-5 p-5 lg:grid-cols-[220px_1fr_270px] lg:p-8">
            <aside className="order-2 grid gap-3 lg:order-1">
              {['公开首页', '护理培训', '知识考核', 'AI助手', '管理后台'].map((item, index) => (
                <div
                  key={item}
                  className={`flex min-h-16 items-center justify-between border border-slate-200 bg-white/72 px-4 text-sm font-semibold text-slate-700 scroll-anim scroll-reveal ${visibleClass}`}
                  style={{ animationDelay: `${0.26 + index * 0.06}s` }}
                >
                  <span>{item}</span>
                  <span className="text-xs text-slate-400">0{index + 1}</span>
                </div>
              ))}
            </aside>

            <div
              className={`order-1 min-h-[390px] border border-slate-300 bg-white/82 p-6 shadow-[0_18px_60px_rgba(148,163,184,0.18)] lg:order-2 lg:p-10 scroll-anim scroll-reveal ${visibleClass}`}
              style={{ animationDelay: '0.28s' }}
            >
              <div className="mx-auto flex max-w-xl flex-col items-center text-center">
                <div className="mb-6 flex h-16 w-16 items-center justify-center border border-slate-300 bg-white shadow-[0_14px_44px_rgba(148,163,184,0.22)]">
                  <Sparkles className="h-7 w-7 text-sky-600" strokeWidth={1.8} />
                </div>
                <p className="mb-4 text-xs font-semibold uppercase tracking-[0.24em] text-slate-500">
                  Nexus Care OS
                </p>
                <h3 className="text-3xl font-semibold tracking-[-0.05em] text-slate-950 sm:text-4xl">
                  让培训资料、学习进度、护理考核与AI辅助在同一系统内协作
                </h3>
                <p className="mt-5 text-sm leading-7 text-slate-600">
                  AI能力聚焦护理培训资料问答、知识总结、学习建议和题目草稿生成。
                </p>
              </div>

              <div className="mt-8 grid gap-3 sm:grid-cols-2">
                {blueprintNodes.map((node) => {
                  const Icon = node.icon;

                  return (
                    <div
                      key={node.label}
                      className={`flex items-start gap-3 border border-slate-200 bg-slate-50/80 p-4 scroll-anim scroll-reveal ${visibleClass}`}
                      style={{ animationDelay: `${0.52 + blueprintNodes.indexOf(node) * 0.06}s` }}
                    >
                      <div className="flex h-10 w-10 shrink-0 items-center justify-center border border-slate-200 bg-white">
                        <Icon className="h-5 w-5 text-sky-600" strokeWidth={1.8} />
                      </div>
                      <div>
                        <p className="text-sm font-semibold text-slate-900">{node.label}</p>
                        <p className="mt-1 text-xs leading-5 text-slate-500">{node.detail}</p>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            <aside className="order-3 grid gap-4">
              <div
                className={`border border-slate-200 bg-white/72 p-5 scroll-anim scroll-reveal ${visibleClass}`}
                style={{ animationDelay: '0.38s' }}
              >
                <div className="mb-5 flex items-center justify-between">
                  <p className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-500">
                    AI Training
                  </p>
                  <BrainCircuit className="h-5 w-5 text-sky-600" strokeWidth={1.8} />
                </div>
                <div className="space-y-4">
                  {['内容问答', '练习生成', '学习建议'].map((item, index) => (
                    <div key={item}>
                      <div className="mb-2 flex justify-between text-xs text-slate-500">
                        <span>{item}</span>
                        <span>{86 - index * 12}%</span>
                      </div>
                      <div className="h-2 bg-slate-100">
                        <div
                          className="h-full bg-slate-900 transition-[width] duration-700 ease-[cubic-bezier(0.16,1,0.3,1)]"
                          style={{
                            width: overviewVisible ? `${86 - index * 12}%` : '0%',
                            transitionDelay: `${0.64 + index * 0.12}s`,
                          }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div
                className={`border border-slate-200 bg-slate-950 p-5 text-white scroll-anim scroll-reveal ${visibleClass}`}
                style={{ animationDelay: '0.52s' }}
              >
                <p className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">
                  Hidden Admin
                </p>
                <p className="mt-4 text-2xl font-semibold tracking-[-0.04em]">RBAC</p>
                <p className="mt-3 text-xs leading-5 text-slate-300">
                  综合管理系统不出现在公开导航，仅管理员登录后进入。
                </p>
              </div>
            </aside>
          </div>

          <div className="relative z-10 grid border-t border-slate-300/80 bg-white/60 sm:grid-cols-2 lg:grid-cols-4">
            {systemMetrics.map((metric, index) => (
              <div
                key={metric.label}
                className={`border-b border-slate-300/70 px-6 py-6 last:border-b-0 sm:border-r sm:last:border-r-0 lg:border-b-0 scroll-anim scroll-reveal ${visibleClass}`}
                style={{ animationDelay: `${0.72 + index * 0.06}s` }}
              >
                <p className="text-3xl font-semibold tracking-[-0.05em] text-slate-950">
                  {metric.value}
                </p>
                <p className="mt-3 text-sm font-semibold text-slate-700">{metric.label}</p>
                <p className="mt-2 text-xs leading-5 text-slate-500">{metric.detail}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}

function ServiceModuleCard({
  module,
  index,
}: {
  module: (typeof moduleCards)[number];
  index: number;
}) {
  const [cardRef, cardVisible] = useInViewOnce<HTMLElement>(0.18);
  const Icon = module.icon;
  const isLarge = index < 2;

  return (
    <article
      ref={cardRef}
      id={module.id}
      className={`group relative scroll-mt-28 overflow-hidden border border-slate-200 bg-slate-50/90 p-6 shadow-[0_18px_70px_rgba(148,163,184,0.14)] transition-all duration-300 hover:-translate-y-1 hover:border-sky-200 hover:bg-white hover:shadow-[0_28px_90px_rgba(125,211,252,0.18)] module-card-anim ${
        cardVisible ? 'is-visible' : ''
      } ${isLarge ? 'min-h-[390px] lg:col-span-3' : 'min-h-[340px] lg:col-span-2'}`}
      style={{ animationDelay: `${0.05 + (index % 3) * 0.05}s` }}
    >
      <div className="pointer-events-none absolute right-5 top-5 h-16 w-16 border border-slate-200/80" />
      <div className="pointer-events-none absolute -right-20 -top-20 h-52 w-52 rounded-full bg-sky-100/80 blur-3xl transition-opacity group-hover:opacity-100" />
      <div className="relative z-10 flex h-full flex-col">
        <div className="mb-7 flex items-start justify-between gap-4">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">
              0{index + 1} / {module.eyebrow}
            </p>
            <h3 className="mt-4 text-4xl font-semibold tracking-[-0.06em] text-slate-950">
              {module.title}
            </h3>
          </div>
          <div className="flex h-12 w-12 shrink-0 items-center justify-center border border-slate-300 bg-white shadow-sm">
            <Icon className="h-6 w-6 text-slate-900" strokeWidth={1.8} />
          </div>
        </div>

        <p className="text-sm leading-7 text-slate-600">{module.summary}</p>

        <div className="mt-8 space-y-3 border-t border-slate-200 pt-5">
          {module.points.map((point) => (
            <div key={point} className="flex items-center gap-3 text-sm text-slate-700">
              <span className="h-px w-8 bg-slate-300" />
              <span>{point}</span>
            </div>
          ))}
        </div>

        <a
          href={`#${module.id}`}
          className="mt-auto inline-flex h-11 w-fit items-center justify-center rounded-full border border-slate-300 bg-white px-5 text-sm font-semibold text-slate-900 transition-colors hover:border-sky-300 hover:bg-sky-50"
        >
          查看模块
        </a>
      </div>
    </article>
  );
}

function ServiceModules() {
  const [modulesRef, modulesVisible] = useInViewOnce<HTMLElement>(0.14);
  const visibleClass = modulesVisible ? 'is-visible' : '';

  return (
    <section
      ref={modulesRef}
      className="relative overflow-hidden bg-white px-5 py-24 sm:px-8 lg:px-12"
    >
      <div className="pointer-events-none absolute inset-x-0 top-0 h-px bg-slate-200" />
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_8%_12%,rgba(186,230,253,0.32),transparent_30%),radial-gradient(circle_at_90%_85%,rgba(226,232,240,0.7),transparent_34%)]" />
      <div className="mx-auto max-w-6xl">
        <div
          className={`relative z-10 mb-12 grid gap-5 md:grid-cols-[1fr_420px] md:items-end scroll-anim scroll-reveal ${visibleClass}`}
          style={{ animationDelay: '0.05s' }}
        >
          <div>
            <p className="mb-3 text-xs font-semibold uppercase tracking-[0.22em] text-sky-600">
              Modular Service Matrix
            </p>
            <h2 className="max-w-2xl text-3xl font-semibold tracking-[-0.05em] text-slate-950 sm:text-5xl">
              5 个内容模块组成 Nexus 的首页能力矩阵
            </h2>
          </div>
          <p className="max-w-md text-sm leading-6 text-slate-600">
            模块不做等宽堆叠，而是像产品蓝图一样错落排布：主要业务更大，支撑能力更轻，视觉上形成从服务到治理的流向。
          </p>
        </div>

        <div className="relative z-10 grid gap-4 lg:grid-cols-6">
          {moduleCards.map((module, index) => (
            <ServiceModuleCard key={module.title} module={module} index={index} />
          ))}
        </div>

        <div
          className={`relative z-10 mt-4 border border-slate-200 bg-slate-950 px-6 py-5 text-white lg:mt-5 scroll-anim scroll-reveal ${visibleClass}`}
          style={{ animationDelay: '0.24s' }}
        >
          <div className="grid gap-4 md:grid-cols-[1fr_auto] md:items-center">
            <p className="text-sm leading-6 text-slate-300">
              管理员后台作为治理能力存在，但不在公开导航中显示；首页只展示可公开理解的产品能力和三类服务入口。
            </p>
            <div className="flex items-center gap-3 text-xs font-semibold uppercase tracking-[0.18em] text-sky-200">
              <ShieldCheck className="h-5 w-5" strokeWidth={1.8} />
              Role Based Access
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

function CyberVideoModule({ onOpenConcept }: { onOpenConcept: () => void }) {
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const containerRef = useRef<HTMLElement | null>(null);
  const [isVideoReady, setIsVideoReady] = useState(false);
  const [isActive, setIsActive] = useState(false);

  useEffect(() => {
    const container = containerRef.current;

    if (!container || !('IntersectionObserver' in window)) {
      setIsActive(true);
      return undefined;
    }

    const observer = new IntersectionObserver(
      ([entry]) => {
        const video = videoRef.current;
        const visible = entry.isIntersecting;
        setIsActive(visible);

        if (!video) {
          return;
        }

        if (visible) {
          video.currentTime = 0;
          video.play().catch(() => undefined);
        } else {
          video.pause();
        }
      },
      { threshold: 0.3 },
    );

    observer.observe(container);

    return () => {
      observer.disconnect();
    };
  }, []);

  const handleVideoEnded = () => {
    const video = videoRef.current;

    video?.pause();
  };

  return (
    <section
      ref={containerRef}
      id="protocol-film"
      className="relative min-h-screen overflow-hidden bg-transparent"
      style={{ minHeight: '100dvh' }}
    >
      <div
        className={`absolute inset-0 bg-cover bg-center transition-opacity duration-1000 ease-in-out ${
          isVideoReady ? 'opacity-0' : 'opacity-60'
        }`}
        style={{ backgroundImage: `url(${VIDEO_POSTER})` }}
        aria-hidden="true"
      />

      <video
        ref={videoRef}
        className={`absolute inset-0 h-full w-full scale-[1.02] object-cover transition-opacity duration-1000 ease-in-out ${
          isVideoReady ? 'opacity-60' : 'opacity-0'
        }`}
        muted
        playsInline
        preload="metadata"
        onCanPlayThrough={() => setIsVideoReady(true)}
        onLoadedData={() => setIsVideoReady(true)}
        onEnded={handleVideoEnded}
      >
        <source src={VIDEO_MP4} type="video/mp4" />
      </video>

      <div className="pointer-events-none absolute inset-0 bg-[linear-gradient(90deg,rgba(238,244,247,0.78)_0%,rgba(238,244,247,0.58)_38%,rgba(238,244,247,0.22)_72%,rgba(238,244,247,0.38)_100%)]" />

      <div className="relative mx-auto flex min-h-screen max-w-7xl items-start px-5 pb-24 pt-[34vh] sm:px-8 sm:pt-[36vh] lg:px-12 lg:pt-[32vh]">
        <div
          className={`mt-10 max-w-2xl scroll-anim scroll-reveal sm:mt-12 lg:mt-14 ${isActive ? 'is-visible' : ''}`}
          style={{ animationDelay: '0.12s' }}
        >
          <p className="mb-4 text-xs font-semibold uppercase tracking-[0.28em] text-slate-500">
            NEXUS AI ASSISTANT
          </p>
          <h2 className="max-w-xl text-4xl font-semibold leading-[1.02] tracking-[-0.055em] text-slate-950 sm:text-6xl lg:text-7xl">
            让智能照护
            <span className="block">更有温度</span>
          </h2>
          <p className="mt-5 max-w-md text-base leading-7 text-slate-600">
            Nexus 让 AI 助手参与培训学习，把分散资料整理成清晰、可靠的护理知识支持。
          </p>
          <div className="mt-8">
            <button
              type="button"
              onClick={onOpenConcept}
              className="inline-flex min-h-12 items-center gap-2 rounded-full bg-slate-950 px-7 text-sm font-semibold text-white shadow-[0_18px_50px_rgba(15,23,42,0.18)] transition-all hover:bg-slate-800 hover:scale-[1.03] active:scale-95"
            >
              了解理念
              <ArrowUpRight className="h-4 w-4" strokeWidth={2} />
            </button>
          </div>
        </div>
      </div>
    </section>
  );
}

function CryptoModuleCard({ module, index }: { module: CryptoModule; index: number }) {
  const [cardRef, cardVisible] = useInViewOnce<HTMLElement>(0.18);
  const Icon = module.icon;

  return (
    <article
      ref={cardRef}
      id={module.id}
      className={`group relative scroll-mt-28 overflow-hidden rounded-lg border border-white/75 bg-white/68 p-6 text-slate-950 shadow-[0_24px_78px_rgba(15,23,42,0.1)] backdrop-blur-xl transition-all duration-300 hover:-translate-y-1 hover:border-slate-300 hover:bg-white/82 hover:shadow-[0_30px_96px_rgba(15,23,42,0.14)] module-card-anim ${
        cardVisible ? 'is-visible' : ''
      } ${module.wide ? 'min-h-[390px] lg:col-span-3' : 'min-h-[340px] lg:col-span-2'}`}
      style={{ animationDelay: `${0.04 + (index % 3) * 0.05}s` }}
    >
      <div className="pointer-events-none absolute right-5 top-5 h-16 w-16 rounded-lg border border-slate-300/70 bg-white/28" />
      <div className="pointer-events-none absolute -right-24 -top-24 h-56 w-56 rounded-full bg-white/80 blur-3xl transition-opacity group-hover:opacity-100" />

      <div className="relative z-10 flex h-full flex-col">
        <div className="mb-7 flex items-start justify-between gap-4">
          <div>
            <p className="text-[11px] font-semibold uppercase tracking-[0.2em] text-slate-500">
              {module.eyebrow}
            </p>
            <h3 className="mt-4 text-4xl font-semibold tracking-[-0.06em] text-slate-950">
              {module.title}
            </h3>
          </div>
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-lg border border-slate-950/10 bg-slate-950 shadow-[0_16px_42px_rgba(15,23,42,0.18)]">
            <Icon className="h-6 w-6 text-white" strokeWidth={1.8} />
          </div>
        </div>

        <p className="max-w-xl text-sm leading-7 text-slate-600">{module.summary}</p>

        <div className="mt-7 border-y border-slate-200/80 py-4">
          <p className="text-xs uppercase tracking-[0.22em] text-slate-400">服务能力</p>
          <p className="mt-2 text-lg font-semibold tracking-[-0.03em] text-slate-950">
            {module.metrics}
          </p>
        </div>

        <div className="mt-6 space-y-3">
          {module.points.map((point) => (
            <div key={point} className="flex items-center gap-3 text-sm text-slate-600">
              <span className="h-px w-8 bg-slate-400/45" />
              <span>{point}</span>
            </div>
          ))}
        </div>

      </div>
    </article>
  );
}

function AssistantDetailModule({ onOpenCapabilities }: { onOpenCapabilities: () => void }) {
  const containerRef = useRef<HTMLElement | null>(null);
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const wasVisibleRef = useRef(false);
  const [isVisible, setIsVisible] = useState(false);
  const [isVideoReady, setIsVideoReady] = useState(false);

  useEffect(() => {
    const container = containerRef.current;

    if (!container || !('IntersectionObserver' in window)) {
      setIsVisible(true);
      videoRef.current?.play().catch(() => undefined);
      return undefined;
    }

    const observer = new IntersectionObserver(
      ([entry]) => {
        const video = videoRef.current;
        const visible = entry.isIntersecting;

        setIsVisible(visible);

        if (!video) {
          wasVisibleRef.current = visible;
          return;
        }

        if (visible && !wasVisibleRef.current) {
          video.currentTime = 0;
          video.play().catch(() => undefined);
        }

        if (!visible) {
          video.pause();
        }

        wasVisibleRef.current = visible;
      },
      { threshold: 0.28 },
    );

    observer.observe(container);

    return () => observer.disconnect();
  }, []);

  return (
    <section
      ref={containerRef}
      id="ai-assistant-detail"
      className="relative min-h-screen overflow-hidden bg-transparent px-5 py-20 sm:px-8 lg:px-12"
      style={{ minHeight: '100dvh' }}
    >
      <div className="relative mx-auto grid min-h-[calc(100dvh-10rem)] max-w-7xl items-center gap-12 lg:grid-cols-[0.86fr_1.14fr]">
        <div
          className={`relative z-20 max-w-xl scroll-anim scroll-reveal ${
            isVisible ? 'is-visible' : ''
          }`}
          style={{ animationDelay: '0.08s' }}
        >
          <p className="mb-5 text-xs font-semibold uppercase tracking-[0.28em] text-slate-500">
            AI ASSISTANT DETAILS
          </p>
          <h2 className="text-5xl font-semibold leading-[0.96] tracking-[-0.07em] text-slate-950 sm:text-6xl lg:text-7xl">
            认识你的
            <span className="block text-slate-500">智能照护助手</span>
          </h2>
          <p className="mt-8 max-w-md text-base leading-8 text-slate-600 sm:text-lg">
            它基于已发布培训资料提供问答、知识总结和学习建议，帮助护工更快理解重点内容。
          </p>
          <div className="mt-9 flex flex-wrap items-center gap-4">
            <button
              type="button"
              onClick={onOpenCapabilities}
              className="inline-flex min-h-14 items-center gap-2 rounded-full border border-slate-950/15 bg-white/60 px-7 text-sm font-semibold text-slate-950 shadow-[0_18px_52px_rgba(15,23,42,0.08)] backdrop-blur-xl transition-all hover:border-sky-400/70 hover:bg-white hover:shadow-[0_22px_60px_rgba(56,189,248,0.18)] active:scale-95"
            >
              查看助手能力
              <ArrowUpRight className="h-4 w-4" strokeWidth={2} />
            </button>
            <span className="text-sm font-medium text-slate-500">
              问答 / 总结 / 学习建议
            </span>
          </div>
        </div>

        <div
          className={`relative z-10 min-h-[420px] overflow-hidden rounded-lg lg:min-h-[680px] overview-play ${
            isVisible ? 'is-visible' : ''
          }`}
        >
          <div className="absolute inset-0 bg-[#eef4f7]" />
          <div
            className={`absolute inset-0 transition-opacity duration-700 ease-out ${
              isVideoReady ? 'opacity-0' : 'opacity-100'
            }`}
          >
            <div className="absolute inset-0 bg-[#eef4f7]" />
            <div className="absolute left-1/2 top-1/2 h-24 w-24 -translate-x-1/2 -translate-y-1/2 rounded-full border border-slate-300/70" />
          </div>

          <video
            ref={videoRef}
            className={`absolute inset-0 h-full w-full scale-[1.06] object-cover object-[58%_center] transition-opacity duration-1000 ease-out will-change-transform ${
              isVideoReady ? 'opacity-90' : 'opacity-0'
            }`}
            muted
            playsInline
            preload="auto"
            onLoadedData={() => setIsVideoReady(true)}
            onCanPlayThrough={() => setIsVideoReady(true)}
            onEnded={() => videoRef.current?.pause()}
          >
            <source src={AI_ASSISTANT_DETAIL_MP4} type="video/mp4" />
          </video>

          <div className="pointer-events-none absolute inset-0 bg-[linear-gradient(90deg,rgba(238,244,247,0.18)_0%,rgba(238,244,247,0.03)_42%,rgba(238,244,247,0.18)_100%)]" />
        </div>
      </div>
    </section>
  );
}

function CaregiverTrainingModule() {
  const [sectionRef, isVisible] = useInViewOnce<HTMLElement>(0.16);
  const visibleClass = isVisible ? 'is-visible' : '';

  return (
    <section
      ref={sectionRef}
      id="caregiver-training"
      className="relative min-h-screen overflow-hidden bg-transparent px-5 py-20 sm:px-8 lg:px-12"
      style={{ minHeight: '100dvh' }}
    >
      <div className="relative mx-auto flex min-h-[calc(100dvh-10rem)] max-w-7xl flex-col items-center justify-center">
        <div
          className={`mx-auto max-w-3xl text-center scroll-anim scroll-reveal ${visibleClass}`}
          style={{ animationDelay: '0.04s' }}
        >
          <p className="mb-4 text-xs font-semibold uppercase tracking-[0.3em] text-slate-500">
            CAREGIVER TRAINING
          </p>
          <h2 className="text-4xl font-semibold leading-[1.02] tracking-[-0.065em] text-slate-950 sm:text-6xl">
            护工培训
            <span className="block text-slate-500">从学习到实操闭环</span>
          </h2>
          <p className="mx-auto mt-5 max-w-xl text-sm leading-7 text-slate-600 sm:text-base">
            Nexus 将课程、AI 辅助练习、素材管理和能力复盘连接起来，让护工在机构与居家场景中持续成长。
          </p>
          <div className="mt-8 flex flex-wrap justify-center gap-3">
            <a
              href="#caregiver-training"
              className="inline-flex min-h-12 items-center gap-2 rounded-full bg-slate-950 px-6 text-sm font-semibold text-white shadow-[0_18px_50px_rgba(15,23,42,0.16)] transition-all hover:bg-slate-800 hover:scale-[1.03] active:scale-95"
            >
              查看培训路径
              <ArrowUpRight className="h-4 w-4" strokeWidth={2} />
            </a>
            <a
              href="#ai-assistant-detail"
              className="inline-flex min-h-12 items-center rounded-full border border-slate-950/12 bg-white/58 px-6 text-sm font-semibold text-slate-800 shadow-[0_14px_42px_rgba(15,23,42,0.08)] backdrop-blur-xl transition-colors hover:bg-white"
            >
              AI 辅助练习
            </a>
          </div>
        </div>

        <div className="relative mt-14 w-full">
          <div className="pointer-events-none absolute left-1/2 top-1/2 h-28 w-[78%] -translate-x-1/2 -translate-y-1/2 rounded-full bg-white/35 blur-3xl" />
          <div className="relative grid gap-4 md:grid-cols-5 md:items-center">
            {trainingCards.map((card, index) => {
              const Icon = card.icon;
              const mutedClass = 'text-slate-600';
              const eyebrowClass = 'text-slate-500';
              const iconClass = 'border-slate-950/10 bg-white/70 text-slate-900';

              return (
                <div
                  key={card.title}
                  className={`module-card-anim ${visibleClass}`}
                  style={{ animationDelay: `${0.14 + index * 0.07}s` }}
                >
                  <article
                    className={`relative min-h-[240px] overflow-hidden rounded-lg border border-white/70 bg-white/46 p-5 text-slate-950 shadow-[0_18px_58px_rgba(15,23,42,0.075)] backdrop-blur-xl transition-transform duration-300 hover:-translate-y-1 hover:bg-white/58 ${card.className}`}
                  >
                    <div className="relative z-10 flex h-full flex-col">
                      <div className="mb-7 flex items-start justify-between gap-4">
                        <div>
                          <p
                            className={`text-[10px] font-semibold uppercase tracking-[0.2em] ${eyebrowClass}`}
                          >
                            {card.eyebrow}
                          </p>
                          <h3 className="mt-3 text-2xl font-semibold tracking-[-0.055em]">
                            {card.title}
                          </h3>
                        </div>
                        <div
                          className={`flex h-11 w-11 shrink-0 items-center justify-center rounded-lg border ${iconClass}`}
                        >
                          <Icon className="h-5 w-5" strokeWidth={1.8} />
                        </div>
                      </div>
                      <p className={`text-sm leading-6 ${mutedClass}`}>{card.summary}</p>
                      <div
                        className="mt-auto pt-7 text-xs font-semibold tracking-[0.14em] text-slate-500"
                      >
                        {card.metric}
                      </div>
                    </div>
                  </article>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </section>
  );
}

function CryptoBlueprintModules() {
  const [sectionRef, sectionVisible] = useInViewOnce<HTMLElement>(0.12);
  const visibleClass = sectionVisible ? 'is-visible' : '';

  return (
    <section
      ref={sectionRef}
      className="relative overflow-hidden bg-transparent px-5 py-24 sm:px-8 lg:px-12"
    >
      <div className="relative mx-auto max-w-6xl">
        <div
          className={`mb-12 grid gap-6 lg:grid-cols-[1fr_420px] lg:items-end scroll-anim scroll-reveal ${visibleClass}`}
          style={{ animationDelay: '0.04s' }}
        >
          <div>
            <p className="mb-3 text-xs font-semibold uppercase tracking-[0.24em] text-slate-500">
              TRAINING PLATFORM
            </p>
            <h2 className="max-w-3xl text-4xl font-semibold tracking-[-0.06em] text-slate-950 sm:text-6xl">
              护理培训，让专业能力持续成长
            </h2>
          </div>
          <p className="text-sm leading-7 text-slate-600">
            Nexus 将培训资料、学习进度、护理考核和AI辅助汇聚到同一平台，
            让管理员高效维护内容，让护工专注学习与成长。
          </p>
        </div>

        <div
          className={`relative mb-5 overflow-hidden rounded-lg border border-white/70 bg-white/50 px-6 py-6 text-slate-950 shadow-[0_24px_78px_rgba(15,23,42,0.08)] backdrop-blur-xl overview-play ${visibleClass}`}
        >
          <div className="relative z-10 grid gap-5 md:grid-cols-[1fr_auto] md:items-center">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.24em] text-slate-500">
                Training command
              </p>
              <p className="mt-3 max-w-2xl text-xl font-semibold tracking-[-0.04em]">
                从资料发布到学习、考核和AI辅助，每一步都有清晰入口和可靠记录。
              </p>
            </div>
            <div className="flex h-16 w-16 items-center justify-center rounded-lg border border-slate-950/10 bg-white/70 shadow-[0_14px_40px_rgba(15,23,42,0.08)]">
              <Sparkles className="h-7 w-7 text-slate-900" strokeWidth={1.8} />
            </div>
          </div>
        </div>

        <div className="relative z-10 grid gap-4 lg:grid-cols-6">
          {cryptoModules.map((module, index) => (
            <CryptoModuleCard key={module.id} module={module} index={index} />
          ))}
        </div>

        <div
          className={`relative z-10 mt-5 rounded-lg border border-white/70 bg-white/42 px-6 py-5 backdrop-blur-xl scroll-anim scroll-reveal ${visibleClass}`}
          style={{ animationDelay: '0.24s' }}
        >
          <div className="grid gap-4 md:grid-cols-[1fr_auto] md:items-center">
            <p className="text-sm leading-6 text-slate-600">
              培训内容由管理员维护，AI生成题目须审核后进入题库，学习与考核结果全程留痕。
            </p>
            <div className="flex items-center gap-3 text-xs font-semibold uppercase tracking-[0.18em] text-slate-900">
              <LockKeyhole className="h-5 w-5" strokeWidth={1.8} />
              Admin reviewed
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

function DetailPageHeader({ label, onHome }: { label: string; onHome: () => void }) {
  return (
    <header className="border-b border-slate-200/80 bg-white/80 backdrop-blur-xl">
      <div className="mx-auto flex min-h-20 max-w-7xl items-center justify-between px-5 sm:px-8 lg:px-12">
        <button
          type="button"
          onClick={onHome}
          className="inline-flex min-h-11 items-center gap-2 text-sm font-semibold text-slate-700 transition-colors hover:text-teal-700"
        >
          <ArrowLeft className="h-4 w-4" />
          返回主页
        </button>
        <div className="text-right">
          <p className="text-sm font-semibold text-slate-950">CareNexus</p>
          <p className="text-xs text-slate-500">{label}</p>
        </div>
      </div>
    </header>
  );
}

function ConceptPage({ onHome, onAssistant }: { onHome: () => void; onAssistant: () => void }) {
  const stages = [
    { number: '01', title: '可信资料', text: '由管理员维护护理培训资料、分类和发布状态，为学习与考核提供统一内容来源。' },
    { number: '02', title: '持续学习', text: '护工按课程章节学习，系统记录学习进度、笔记与完成情况，让成长过程清晰可见。' },
    { number: '03', title: '知识考核', text: '课程作业与考试检验掌握程度，客观题自动评分，成绩与培训状态同步留存。' },
    { number: '04', title: '智能辅助', text: 'AI围绕已发布资料提供问答、总结和建议，帮助理解知识，但不替代专业判断。' },
  ];

  return (
    <div className="min-h-screen bg-[#eef4f7] text-slate-950">
      <DetailPageHeader label="项目理念" onHome={onHome} />
      <main>
        <section className="border-b border-slate-200/80 bg-white/55">
          <div className="mx-auto max-w-7xl px-5 py-20 sm:px-8 sm:py-28 lg:px-12">
            <p className="text-xs font-semibold uppercase tracking-[0.28em] text-teal-700">CARE WITH CLARITY</p>
            <h1 className="mt-6 max-w-4xl text-5xl font-semibold leading-[1.02] tracking-[-0.06em] sm:text-7xl">
              让护理知识真正进入日常工作
            </h1>
            <p className="mt-8 max-w-2xl text-lg leading-8 text-slate-600">
              CareNexus以护理培训为核心，把内容管理、学习过程、知识考核和智能辅助连接成完整闭环，让管理员更容易维护可信内容，让护工更专注地学习和应用。
            </p>
          </div>
        </section>

        <section className="mx-auto max-w-7xl px-5 py-20 sm:px-8 lg:px-12">
          <div className="mb-10 max-w-2xl">
            <p className="text-xs font-semibold uppercase tracking-[0.24em] text-slate-500">TRAINING LOOP</p>
            <h2 className="mt-4 text-3xl font-semibold tracking-[-0.04em] sm:text-5xl">一条清晰、可追踪的培训路径</h2>
          </div>
          <div className="grid gap-px overflow-hidden rounded-lg border border-slate-200 bg-slate-200 md:grid-cols-2">
            {stages.map((stage) => (
              <article key={stage.number} className="min-h-64 bg-white p-8 sm:p-10">
                <p className="text-sm font-semibold text-teal-700">{stage.number}</p>
                <h3 className="mt-8 text-2xl font-semibold">{stage.title}</h3>
                <p className="mt-4 max-w-md leading-7 text-slate-600">{stage.text}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="border-y border-slate-200 bg-slate-950 text-white">
          <div className="mx-auto grid max-w-7xl gap-10 px-5 py-16 sm:px-8 lg:grid-cols-2 lg:px-12">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.24em] text-teal-300">ADMINISTRATOR</p>
              <h2 className="mt-4 text-3xl font-semibold">管理员维护标准</h2>
              <p className="mt-5 max-w-lg leading-7 text-slate-300">统一管理课程资料、题库、考试和AI题目草稿，保证培训内容经过审核后再进入正式学习流程。</p>
            </div>
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.24em] text-sky-300">CAREGIVER</p>
              <h2 className="mt-4 text-3xl font-semibold">护工专注成长</h2>
              <p className="mt-5 max-w-lg leading-7 text-slate-300">通过章节、作业、考试、笔记和学习记录持续积累专业能力，并在需要时获得基于资料的AI辅助。</p>
            </div>
          </div>
        </section>

        <section className="mx-auto flex max-w-7xl flex-col items-start justify-between gap-8 px-5 py-20 sm:px-8 md:flex-row md:items-center lg:px-12">
          <div>
            <p className="text-sm font-semibold text-teal-700">下一步</p>
            <h2 className="mt-2 text-3xl font-semibold tracking-[-0.04em]">了解AI如何辅助护理学习</h2>
          </div>
          <button type="button" onClick={onAssistant} className="inline-flex min-h-12 items-center gap-2 rounded-full bg-slate-950 px-7 text-sm font-semibold text-white transition-colors hover:bg-teal-700">
            查看助手能力 <ArrowUpRight className="h-4 w-4" />
          </button>
        </section>
      </main>
    </div>
  );
}

function AssistantCapabilitiesPage({ onHome }: { onHome: () => void }) {
  const capabilities = [
    { icon: Bot, title: '资料问答', text: '围绕当前课程已发布资料回答问题，并保留资料来源，便于学习者核对重要信息。' },
    { icon: Database, title: '知识总结', text: '从课程内容中提炼关键步骤、注意事项和复习重点，帮助快速建立知识结构。' },
    { icon: Activity, title: '学习建议', text: '结合课程完成情况和练习表现，给出下一步复习方向与学习顺序建议。' },
    { icon: BrainCircuit, title: '练习辅助', text: '依据培训资料生成选择题和判断题草稿，经管理员审核后才可进入正式题库。' },
  ];

  return (
    <div className="min-h-screen bg-[#eef4f7] text-slate-950">
      <DetailPageHeader label="AI学习助手" onHome={onHome} />
      <main>
        <section className="mx-auto max-w-7xl px-5 py-20 sm:px-8 sm:py-28 lg:px-12">
          <div className="grid items-end gap-10 lg:grid-cols-[1.15fr_0.85fr]">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.28em] text-teal-700">AI LEARNING ASSISTANT</p>
              <h1 className="mt-6 max-w-4xl text-5xl font-semibold leading-[1.02] tracking-[-0.06em] sm:text-7xl">让每一次提问都有资料依据</h1>
            </div>
            <p className="max-w-xl text-lg leading-8 text-slate-600">
              AI学习助手服务于护理培训过程，帮助护工理解资料、提炼重点和安排复习。它不提供疾病诊断、处方或自动医疗决策。
            </p>
          </div>
        </section>

        <section className="border-y border-slate-200 bg-white/65">
          <div className="mx-auto grid max-w-7xl gap-px bg-slate-200 md:grid-cols-2 lg:grid-cols-4">
            {capabilities.map(({ icon: Icon, title, text }) => (
              <article key={title} className="min-h-80 bg-white p-8">
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-slate-950 text-white">
                  <Icon className="h-6 w-6" strokeWidth={1.8} />
                </div>
                <h2 className="mt-10 text-2xl font-semibold">{title}</h2>
                <p className="mt-5 leading-7 text-slate-600">{text}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="mx-auto grid max-w-7xl gap-12 px-5 py-20 sm:px-8 lg:grid-cols-2 lg:px-12">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.24em] text-slate-500">HOW IT WORKS</p>
            <h2 className="mt-4 text-4xl font-semibold tracking-[-0.04em]">从可信资料到清晰回答</h2>
          </div>
          <ol className="space-y-8">
            {['选择当前课程的已发布培训资料', '提出问题或选择总结、建议等能力', '助手依据资料生成内容并标注来源', '学习者核查重点信息后用于复习'].map((item, index) => (
              <li key={item} className="flex gap-5 border-b border-slate-200 pb-7">
                <span className="font-semibold text-teal-700">0{index + 1}</span>
                <span className="text-lg font-medium">{item}</span>
              </li>
            ))}
          </ol>
        </section>

        <section className="bg-slate-950 text-white">
          <div className="mx-auto max-w-7xl px-5 py-16 sm:px-8 lg:px-12">
            <p className="text-xs font-semibold uppercase tracking-[0.24em] text-amber-300">RESPONSIBLE BOUNDARY</p>
            <h2 className="mt-4 text-3xl font-semibold">辅助学习，不替代专业判断</h2>
            <p className="mt-5 max-w-3xl leading-8 text-slate-300">助手内容来自培训资料，可能存在遗漏或偏差。护理人员应核查重要信息，并始终遵循机构制度、课程规范以及专业医护人员指导。</p>
          </div>
        </section>
      </main>
    </div>
  );
}

function App() {
  const mouse = useRef<Point>({ x: -999, y: -999 });
  const smooth = useRef<Point>({ x: -999, y: -999 });
  const rafRef = useRef<number | null>(null);
  const assistantHomeScrollRef = useRef<number | null>(null);
  const [cursorPos, setCursorPos] = useState<Point>({ x: -999, y: -999 });
  const [portalRoute, setPortalRoute] = useState<AppRoute>(() => readPortalRoute());

  useEffect(() => {
    const syncRoute = () => setPortalRoute(readPortalRoute());
    window.addEventListener('hashchange', syncRoute);
    return () => window.removeEventListener('hashchange', syncRoute);
  }, []);

  useEffect(() => {
    updatePortalSeo(portalRoute);
  }, [portalRoute]);

  useEffect(() => {
    if (portalRoute === 'home' && assistantHomeScrollRef.current !== null) {
      const scrollTop = assistantHomeScrollRef.current;
      assistantHomeScrollRef.current = null;
      requestAnimationFrame(() => {
        window.scrollTo({ top: scrollTop, left: 0, behavior: 'auto' });
      });
      return;
    }

    window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
  }, [portalRoute]);

  useEffect(() => {
    if (portalRoute !== 'home') {
      return undefined;
    }
    const handleMouseMove = (event: MouseEvent) => {
      mouse.current.x = event.clientX;
      mouse.current.y = event.clientY;
    };

    const animate = () => {
      smooth.current.x += (mouse.current.x - smooth.current.x) * 0.1;
      smooth.current.y += (mouse.current.y - smooth.current.y) * 0.1;
      setCursorPos({ x: smooth.current.x, y: smooth.current.y });
      rafRef.current = requestAnimationFrame(animate);
    };

    window.addEventListener('mousemove', handleMouseMove);
    rafRef.current = requestAnimationFrame(animate);

    return () => {
      window.removeEventListener('mousemove', handleMouseMove);
      if (rafRef.current !== null) {
        cancelAnimationFrame(rafRef.current);
      }
    };
  }, [portalRoute]);

  function navigate(route: AppRoute) {
    setPortalRoute(route);
    window.location.hash = route === 'home' ? 'home' : route;
  }

  function openAssistantFromHome() {
    assistantHomeScrollRef.current = window.scrollY;
    navigate('assistant');
  }

  if (portalRoute === 'concept') {
    return <ConceptPage onHome={() => navigate('home')} onAssistant={() => navigate('assistant')} />;
  }

  if (portalRoute === 'assistant') {
    return <AssistantCapabilitiesPage onHome={() => navigate('home')} />;
  }

  if (portalRoute === 'login' || portalRoute === 'workspace') {
    return <Portal route={portalRoute} onHome={() => navigate('home')} onRouteChange={navigate} />;
  }

  return (
    <div
      className="relative min-h-screen bg-[#eef4f7] tracking-[-0.02em]"
      style={{ fontFamily: "'Inter', sans-serif" }}
    >
      <div className="pointer-events-none fixed inset-0 z-0 bg-[#eef4f7]" aria-hidden="true" />

      <div className="relative z-10">
        <Navigation onOpenPortal={() => navigate('login')} />

        <section
          id="home"
          className="relative w-full overflow-hidden h-screen bg-[#eef4f7]"
          style={{ height: '100dvh' }}
        >
          <div
            className="absolute inset-0 bg-center bg-cover bg-no-repeat z-10 hero-zoom"
            style={{ backgroundImage: `url(${BG_IMAGE_1})` }}
          />

          <RevealLayer image={BG_IMAGE_2} cursorX={cursorPos.x} cursorY={cursorPos.y} />

          <div className="absolute inset-0 z-40 pointer-events-none bg-[radial-gradient(circle_at_50%_42%,rgba(255,255,255,0.08),rgba(248,250,252,0.42)_48%,rgba(241,245,249,0.72)_100%)]" />
          <div className="absolute inset-x-0 top-0 z-40 h-32 pointer-events-none bg-gradient-to-b from-white/78 to-transparent" />
          <div className="absolute inset-x-0 bottom-0 z-40 h-52 pointer-events-none bg-gradient-to-t from-white/82 to-transparent" />

          <div className="absolute top-[14%] left-0 right-0 flex flex-col items-center text-center px-5 pointer-events-none z-50">
            <h1 className="text-slate-950 leading-[0.95] drop-shadow-[0_1px_0_rgba(255,255,255,0.65)]">
              <span
                className="block font-playfair italic font-normal text-5xl sm:text-7xl md:text-8xl hero-anim hero-reveal"
                style={{ letterSpacing: '-0.05em', animationDelay: '0.25s' }}
              >
                Genes hold
              </span>
              <span
                className="block font-normal text-5xl sm:text-7xl md:text-8xl -mt-1 hero-anim hero-reveal"
                style={{ letterSpacing: '-0.08em', animationDelay: '0.42s' }}
              >
                futures of life
              </span>
            </h1>
          </div>

          <div
            className="hidden sm:block absolute bottom-14 left-10 md:left-14 max-w-[260px] z-50 hero-anim hero-fade"
            style={{ animationDelay: '0.7s' }}
          >
            <p className="text-sm text-slate-600 leading-relaxed">
              每一次学习都在积累护理能力，Nexus 将资料、练习与考核连接成清晰的成长路径。
            </p>
          </div>

          <div
            className="absolute bottom-10 sm:bottom-24 left-5 right-5 sm:left-auto sm:right-10 md:right-14 max-w-full sm:max-w-[260px] flex flex-col items-start gap-4 sm:gap-5 z-50 hero-anim hero-fade"
            style={{ animationDelay: '0.85s' }}
          >
            <p className="text-xs sm:text-sm text-slate-600 leading-relaxed">
              探索护理培训、知识考核与AI辅助，让专业知识在日常学习中持续生长。
            </p>
          </div>
        </section>

        <CyberVideoModule onOpenConcept={() => navigate('concept')} />
        <AssistantDetailModule onOpenCapabilities={openAssistantFromHome} />
        <CryptoBlueprintModules />
      </div>
    </div>
  );
}

function readPortalRoute() {
  if (window.location.hash === '#concept') return 'concept';
  if (window.location.hash === '#assistant') return 'assistant';
  if (window.location.hash === '#login') return 'login';
  if (window.location.hash === '#workspace') return 'workspace';
  return 'home';
}

export default App;
