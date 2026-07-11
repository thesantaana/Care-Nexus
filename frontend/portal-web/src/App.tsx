import { useEffect, useRef, useState } from 'react';
import type { LucideIcon } from 'lucide-react';
import {
  Activity,
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

const VIDEO_WEBM = '/assets/bg-video.webm';

const VIDEO_MP4 = '/assets/bg-video.mp4';

const VIDEO_POSTER = '/assets/first-frame.jpg';

const AI_ASSISTANT_DETAIL_WEBM = '/assets/ai-assistant-detail.webm';

const AI_ASSISTANT_DETAIL_MP4 = '/assets/ai-assistant-detail.mp4';

const SPOTLIGHT_R = 260;

type Point = {
  x: number;
  y: number;
};

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
    id: 'doctor-record-review',
    title: '健康档案研判',
    eyebrow: '01 / CLINICAL PROFILE',
    icon: Database,
    summary:
      '整合老人健康档案、慢病史、用药记录与护理反馈，医生可以快速形成连续、准确的判断。',
    metrics: '360° 健康视图',
    points: ['长期趋势追踪', '用药与既往史关联', '护理记录同步'],
    wide: true,
  },
  {
    id: 'doctor-warning',
    title: '健康预警中心',
    eyebrow: '02 / RISK WARNING',
    icon: ShieldCheck,
    summary:
      '对异常指标、重点人群和风险变化进行分层提示，让医生先一步发现问题、安排干预。',
    metrics: '高风险优先队列',
    points: ['异常波动识别', '重点人群标记', '干预建议生成'],
    wide: true,
  },
  {
    id: 'doctor-followup',
    title: '重点人群随访',
    eyebrow: '03 / FOLLOW-UP CARE',
    icon: Activity,
    summary:
      '将随访计划、服务反馈和健康变化串联起来，医生能持续掌握老人状态。',
    metrics: '随访计划自动编排',
    points: ['随访任务提醒', '重点人群分层', '结果回流档案'],
  },
  {
    id: 'doctor-intervention',
    title: '干预记录闭环',
    eyebrow: '04 / INTERVENTION LOOP',
    icon: LockKeyhole,
    summary:
      '医生的评估、建议与处置记录沉淀为闭环证据，便于复盘、交接和持续管理。',
    metrics: '医护协作留痕',
    points: ['评估结论记录', '干预过程追踪', '交接信息清晰'],
  },
  {
    id: 'doctor-ai-assessment',
    title: 'AI 辅助评估',
    eyebrow: '05 / AI ASSESSMENT',
    icon: BrainCircuit,
    summary:
      'AI 将档案、预警和随访信息整理成医生可读的摘要，辅助更快完成专业判断。',
    metrics: '结构化病情摘要',
    points: ['风险摘要', '健康评估建议', '医生确认后生效'],
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
  const navItems = [
    { label: '首页', href: '#home' },
    { label: '护理平台', href: '#ai-consulting' },
  ];

  return (
    <nav className="fixed top-0 left-0 right-0 z-[100] flex items-center justify-between p-4 sm:p-5">
      <a href="#home" className="flex items-center gap-2.5" aria-label="CareNexus 首页">
        <svg width="26" height="26" viewBox="0 0 256 256" fill="#1f2937" aria-hidden="true">
          <path d="M 256 256 L 128 256 L 0 128 L 128 128 Z M 256 128 L 128 128 L 0 0 L 128 0 Z" />
        </svg>
        <span className="text-gray-900 text-2xl font-playfair italic">CareNexus</span>
      </a>

      <div className="hidden md:flex absolute left-1/2 -translate-x-1/2 bg-white/70 backdrop-blur-xl border border-slate-300/70 shadow-[0_18px_60px_rgba(148,163,184,0.22)] rounded-full px-2 py-2 items-center gap-1">
        {navItems.map((item) => (
          <a
            key={item.label}
            href={item.href}
            className={`px-4 py-1.5 rounded-full text-sm font-medium ${
              item.label === '首页'
                ? 'bg-slate-900 text-white shadow-sm shadow-slate-400/30'
                : 'text-slate-600 hover:bg-white/80 hover:text-slate-950 transition-colors'
            }`}
          >
            {item.label}
          </a>
        ))}
      </div>

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
              {['公开首页', '护工服务', '护理平台', '医生服务', '后台隐藏'].map((item, index) => (
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
                  让培训、照护、随访与权限管理在同一系统内协作
                </h3>
                <p className="mt-5 text-sm leading-7 text-slate-600">
                  MVP 先让护理培训系统获得 AI 能力，后续再把智能能力扩展到移动护理、医生健康管理和综合后台。
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

function CyberVideoModule() {
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
        <source src={VIDEO_WEBM} type="video/webm" />
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
            Nexus 让 AI 助手参与培训、护理与医生随访，把复杂流程整理成清晰、可靠的照护协作。
          </p>
          <div className="mt-8">
            <a
              href="#liquidity-network"
              className="inline-flex min-h-12 items-center gap-2 rounded-full bg-slate-950 px-7 text-sm font-semibold text-white shadow-[0_18px_50px_rgba(15,23,42,0.18)] transition-all hover:bg-slate-800 hover:scale-[1.03] active:scale-95"
            >
              了解理念
              <ArrowUpRight className="h-4 w-4" strokeWidth={2} />
            </a>
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

        <a
          href={`#${module.id}`}
          className="mt-auto inline-flex min-h-11 w-fit items-center gap-2 rounded-full border border-slate-950/10 bg-white/52 px-5 text-sm font-semibold text-slate-950 shadow-[0_12px_34px_rgba(15,23,42,0.08)] transition-colors hover:bg-white"
        >
          查看能力
          <ArrowUpRight className="h-4 w-4" strokeWidth={2} />
        </a>
      </div>
    </article>
  );
}

function AssistantDetailModule() {
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
            它把培训提醒、服务记录、健康预警与随访建议整理成清晰线索，帮助每个角色更快理解下一步。
          </p>
          <div className="mt-9 flex flex-wrap items-center gap-4">
            <a
              href="#liquidity-network"
              className="inline-flex min-h-14 items-center gap-2 rounded-full border border-slate-950/15 bg-white/60 px-7 text-sm font-semibold text-slate-950 shadow-[0_18px_52px_rgba(15,23,42,0.08)] backdrop-blur-xl transition-all hover:border-sky-400/70 hover:bg-white hover:shadow-[0_22px_60px_rgba(56,189,248,0.18)] active:scale-95"
            >
              查看助手能力
              <ArrowUpRight className="h-4 w-4" strokeWidth={2} />
            </a>
            <span className="text-sm font-medium text-slate-500">
              培训 / 护理 / 随访协同
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
            <source src={AI_ASSISTANT_DETAIL_WEBM} type="video/webm" />
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
              DOCTOR SERVICE
            </p>
            <h2 className="max-w-3xl text-4xl font-semibold tracking-[-0.06em] text-slate-950 sm:text-6xl">
              医生服务，让专业判断更快抵达
            </h2>
          </div>
          <p className="text-sm leading-7 text-slate-600">
            Nexus 将健康档案、预警、随访和干预记录整理成医生可快速理解的工作台，
            让医生保持专业主导，同时减少重复整理和信息遗漏。
          </p>
        </div>

        <div
          className={`relative mb-5 overflow-hidden rounded-lg border border-white/70 bg-white/50 px-6 py-6 text-slate-950 shadow-[0_24px_78px_rgba(15,23,42,0.08)] backdrop-blur-xl overview-play ${visibleClass}`}
        >
          <div className="relative z-10 grid gap-5 md:grid-cols-[1fr_auto] md:items-center">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.24em] text-slate-500">
                Clinical command
              </p>
              <p className="mt-3 max-w-2xl text-xl font-semibold tracking-[-0.04em]">
                重要信息自动汇总到医生视角：谁需要随访、哪里出现风险、上一次干预是否有效，一眼就能接上判断。
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
              医生服务模块强调专业能力：持续健康档案、风险预警、随访干预和 AI 辅助评估都由医生确认后进入服务闭环。
            </p>
            <div className="flex items-center gap-3 text-xs font-semibold uppercase tracking-[0.18em] text-slate-900">
              <LockKeyhole className="h-5 w-5" strokeWidth={1.8} />
              Doctor verified
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

function SkyConsultingHero({ onLogin }: { onLogin: () => void }) {
  return (
    <section id="ai-consulting" className="sky-consulting-hero" aria-label="Nexus AI 智能照护能力展示">
      <div className="sky-cloud sky-cloud-left" aria-hidden="true" />
      <div className="sky-cloud sky-cloud-right" aria-hidden="true" />
      <div className="sky-cloud sky-cloud-bottom" aria-hidden="true" />

      <div className="sky-hero-copy">
        <h1>Building the future with<br /><span>AI and strategy</span></h1>
        <p>We help organizations unlock growth and efficiency<br />through data-driven consulting and intelligent automation.</p>
        <div className="sky-hero-actions">
          <button type="button">VIEW DEMO</button>
          <button type="button" onClick={onLogin}>GET STARTED <span><ArrowUpRight aria-hidden="true" /></span></button>
        </div>
      </div>

      <div className="sky-card-orbit" aria-label="Nexus 智能能力概览">
        <div className="sky-orbit-card sky-orbit-ghost sky-orbit-ghost-left"><span>CareHub</span><span>AI Message</span></div>
        <div className="sky-orbit-card sky-orbit-performance"><small>Performance <b>↗</b></small><strong>49%</strong><span>Business growth</span><i /><div><em>Strategic</em><em>AI-Focused</em></div></div>
        <div className="sky-orbit-card sky-orbit-points"><div><em>Strategic</em><em>AI-First</em><em>Smarter</em><em>Grow Faster</em></div><span>Data Points</span><strong>520k+</strong></div>
        <div className="sky-orbit-card sky-orbit-training"><b>+</b><strong>Data training</strong><span>Upload your content</span></div>
        <div className="sky-orbit-card sky-orbit-expertise"><p>Expertise <i /> that<br />Combines Strategy,<br /><strong>Data,</strong> and Artificial<br />Intelligence</p></div>
        <div className="sky-orbit-card sky-orbit-chart"><small>Intelligence in<br />Every Decision</small><div className="sky-area-chart" /></div>
        <div className="sky-orbit-card sky-orbit-ghost sky-orbit-ghost-right" />
      </div>

      <div className="sky-rating">Rated 4.9/5 by 4,900+ clients <span>★ ★ ★ ★ ★</span></div>
    </section>
  );
}

function App() {
  const mouse = useRef<Point>({ x: -999, y: -999 });
  const smooth = useRef<Point>({ x: -999, y: -999 });
  const rafRef = useRef<number | null>(null);
  const [cursorPos, setCursorPos] = useState<Point>({ x: -999, y: -999 });
  const [portalRoute, setPortalRoute] = useState<'home' | 'login' | 'workspace'>(() => readPortalRoute());

  useEffect(() => {
    const syncRoute = () => setPortalRoute(readPortalRoute());
    window.addEventListener('hashchange', syncRoute);
    return () => window.removeEventListener('hashchange', syncRoute);
  }, []);

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

  function navigate(route: 'home' | 'login' | 'workspace') {
    setPortalRoute(route);
    window.location.hash = route === 'home' ? 'home' : route;
  }

  if (portalRoute !== 'home') {
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
              每一段生命信息都记录着照护需求、健康变化与服务轨迹，Nexus 将它们连接为可理解、可协同的智能医疗网络。
            </p>
          </div>

          <div
            className="absolute bottom-10 sm:bottom-24 left-5 right-5 sm:left-auto sm:right-10 md:right-14 max-w-full sm:max-w-[260px] flex flex-col items-start gap-4 sm:gap-5 z-50 hero-anim hero-fade"
            style={{ animationDelay: '0.85s' }}
          >
            <p className="text-xs sm:text-sm text-slate-600 leading-relaxed">
              滑过基因链，揭示从护理培训到医生随访的服务流转，让数据、人员与 AI 能力在同一系统中持续生长。
            </p>
            <button
              className="bg-slate-900 hover:bg-sky-500 text-white text-sm font-medium px-7 py-3 rounded-full transition-all hover:scale-[1.03] active:scale-95 hover:shadow-lg hover:shadow-sky-300/45"
              type="button"
              onClick={() => navigate('login')}
            >
              登录并进入
            </button>
          </div>
        </section>

        <CyberVideoModule />
        <AssistantDetailModule />
        <SkyConsultingHero onLogin={() => navigate('login')} />
        <CryptoBlueprintModules />
      </div>
    </div>
  );
}

function readPortalRoute() {
  if (window.location.hash === '#login') return 'login';
  if (window.location.hash === '#workspace') return 'workspace';
  return 'home';
}

export default App;
