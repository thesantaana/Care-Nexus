import { useEffect, useMemo, useState, type FormEvent } from 'react';
import {
  ArrowLeft, BookOpen, CheckCircle2, ClipboardCheck, FileText, GraduationCap,
  Lightbulb, ListChecks, ListTree, LogOut, MessageCircle, MessageSquareText,
  Heart, RefreshCw, Reply, Send, Sparkles, Target, Trash2, XCircle,
} from 'lucide-react';
import { api, type CurrentUser, type TrainingResource } from './portal';
import { resolveUserProfile } from './userProfile';

type CourseSection = 'ai' | 'chapters' | 'discussions' | 'assignments' | 'exam' | 'mistakes' | 'records';
type CourseScore = { resourceId: number; examId: number | null; passScore: number; bestScore: number; passed: boolean };
type ScoreSummary = { averageScore: number; trainingPassed: boolean; courseScores: CourseScore[] } | null;
type Discussion = { id: string; title: string; content: string; authorName: string; replyCount: number; likeCount: number; liked: boolean; ownedByCurrentUser: boolean; createdAt: string };
type DiscussionReply = { id: string; content: string; authorName: string; parentReplyId?: string; replyToAuthorName?: string; likeCount: number; liked: boolean; ownedByCurrentUser: boolean; createdAt: string };
type Assignment = { id: number; title: string; type: string; content: string; optionsJson?: string; submissionStatus?: string; submittedAnswer?: string; score?: number; correctAnswer?: string };
type ExamOption = { id: number; optionLabel: string; optionContent: string };
type ExamQuestion = { questionId: number; score: number; question: { questionType: string; questionContent: string; options: ExamOption[] } };
type Exam = { id: number; examName: string; passScore: number; maxAttempts: number; questions: ExamQuestion[] };
type CourseLearningStatus = { resourceId: number; learnedSeconds: number; requiredSeconds: number; completed: boolean };
type ListFilter = 'all' | 'completed' | 'pending';
type MistakeQuestion = { answerId: number; questionId: number; questionType: string; questionContent: string; userAnswer: string; standardAnswer: string; analysis?: string; submittedAt: string };

export function CourseWorkspace({ user, token, resource, scoreSummary, onBack, onLogout }: {
  user: CurrentUser; token: string; resource: TrainingResource; scoreSummary: ScoreSummary;
  onBack: () => void; onLogout: () => void;
}) {
  const [section, setSection] = useState<CourseSection>('chapters');
  const userProfile = useMemo(() => resolveUserProfile(user), [user]);
  const score = scoreSummary?.courseScores.find((item) => item.resourceId === resource.id);
  const navigation = [
    { id: 'ai' as const, label: 'AI助教', icon: Sparkles },
    { id: 'chapters' as const, label: '章节', icon: ListChecks },
    { id: 'discussions' as const, label: '讨论', icon: MessageCircle },
    { id: 'assignments' as const, label: '作业', icon: FileText },
    { id: 'exam' as const, label: '考试', icon: GraduationCap },
    { id: 'mistakes' as const, label: '错题集', icon: XCircle },
    { id: 'records' as const, label: '学习记录', icon: BookOpen },
  ];

  return <main className="course-workspace-enter min-h-screen bg-[#f1f4f8] pl-[78px] text-slate-900 md:pl-[220px]">
    <header className="fixed inset-x-0 top-0 z-30 flex h-16 items-center justify-between border-b border-slate-200 bg-white px-5 md:pl-[244px]">
      <button className="inline-flex items-center gap-2 text-sm font-medium text-slate-600 hover:text-teal-700" onClick={onBack}><ArrowLeft size={18} />全部课程</button>
      <div className="flex items-center gap-3"><img className="h-9 w-9 rounded-full object-cover" src={userProfile.avatarDataUrl} alt={`${userProfile.displayName}的头像`} /><strong className="hidden text-sm sm:block">{userProfile.displayName}</strong><button className="rounded-md p-2 text-slate-500 hover:bg-slate-100" onClick={onLogout} title="退出登录"><LogOut size={18} /></button></div>
    </header>
    <aside className="fixed inset-y-0 left-0 z-40 w-[78px] border-r border-slate-200 bg-white pt-4 md:w-[220px]">
      <div className="px-3 md:px-5"><a className="flex items-center gap-3 rounded-md outline-none transition hover:opacity-80 focus-visible:ring-2 focus-visible:ring-teal-600" href="/" aria-label="返回 CareNexus 主页" title="返回主页"><span className="grid h-10 w-10 shrink-0 place-items-center rounded-md bg-teal-700 text-white"><CheckCircle2 size={20} /></span><strong className="hidden md:block">CareNexus</strong></a><div className="mt-6 hidden md:block"><img className="h-24 w-full rounded-md object-cover" src={resource.coverUrl || '/assets/default-course-cover.png'} alt="" /><h2 className="mt-3 line-clamp-2 text-sm font-semibold">{resource.title}</h2></div></div>
      <nav className="mt-7 grid" aria-label="课程导航">{navigation.map((item) => <button key={item.id} className={`flex min-h-14 items-center justify-center gap-3 border-l-4 px-3 text-sm transition md:justify-start md:px-6 ${section === item.id ? 'border-teal-600 bg-teal-50 font-semibold text-teal-700' : 'border-transparent text-slate-500 hover:bg-slate-50'}`} onClick={() => setSection(item.id)}><item.icon size={19} /><span className="hidden md:inline">{item.label}</span></button>)}</nav>
    </aside>
    <section className="min-h-screen px-5 pb-12 pt-24 sm:px-8 lg:px-12">
      <div className="course-panel-enter mx-auto max-w-6xl" key={section}>
        {section === 'ai' && <AiTutor resource={resource} token={token} />}
        {section === 'chapters' && <Chapters userId={user.userId} resource={resource} score={score} token={token} />}
        {section === 'discussions' && <SocialDiscussions resourceId={resource.id} token={token} />}
        {section === 'assignments' && <Assignments resource={resource} token={token} />}
        {section === 'exam' && <ExamPanel resourceId={resource.id} score={score} token={token} onUpdated={() => window.location.reload()} />}
        {section === 'mistakes' && <MistakeBook resourceId={resource.id} token={token} />}
        {section === 'records' && <LearningRecords user={user} token={token} resource={resource} score={score} summary={scoreSummary} />}
      </div>
    </section>
  </main>;
}

function PageTitle({ eyebrow, title, description }: { eyebrow: string; title: string; description: string }) {
  return <header className="mb-7"><p className="text-xs font-bold uppercase tracking-[0.18em] text-teal-700">{eyebrow}</p><h1 className="mt-2 text-3xl font-semibold">{title}</h1><p className="mt-2 text-sm text-slate-500">{description}</p></header>;
}

type AiTutorTool = 'qa' | 'summary' | 'suggestions' | 'practice';
type PracticeQuestion = { question: string; options: string[]; answer: number; explanation: string };
type SuggestedQuestion = { question: string; answer: string };

function AiTutor({ resource, token }: { resource: TrainingResource; token: string }) {
  const [tool, setTool] = useState<AiTutorTool>('qa');
  const [question, setQuestion] = useState('');
  const [content, setContent] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [practiceIndex, setPracticeIndex] = useState(0);
  const [practiceAnswer, setPracticeAnswer] = useState<number | null>(null);
  const [correctCount, setCorrectCount] = useState(0);
  const [practiceQuestions, setPracticeQuestions] = useState<PracticeQuestion[]>([]);
  const suggestedQuestions = useMemo(() => courseSuggestedQuestions(resource), [resource]);

  const tools: Array<{ id: AiTutorTool; title: string; description: string; icon: typeof Sparkles; tone: string }> = [
    { id: 'qa', title: '资料问答', description: '围绕当前课程资料提问，快速定位规范与操作要点。', icon: MessageSquareText, tone: 'bg-violet-50 text-violet-600' },
    { id: 'summary', title: '重点总结', description: '提炼章节重点、风险信号和需要牢记的流程。', icon: ListTree, tone: 'bg-sky-50 text-sky-600' },
    { id: 'suggestions', title: '学习建议', description: '结合课程内容生成复习顺序和学习建议。', icon: Lightbulb, tone: 'bg-amber-50 text-amber-600' },
    { id: 'practice', title: '模拟陪练', description: '通过课程专属练习巩固知识并获得即时讲解。', icon: Target, tone: 'bg-emerald-50 text-emerald-600' },
  ];

  function chooseTool(next: AiTutorTool) {
    setTool(next);
    setContent('');
    setError('');
    if (next === 'practice') void loadPractice();
  }

  function askSuggestedQuestion(item: SuggestedQuestion) {
    setQuestion(item.question);
    void generate('qa', item.question);
  }

  async function loadPractice() {
    if (loading) return;
    resetPractice();
    setLoading(true);
    setError('');
    try {
      const response = await api<{ questions: PracticeQuestion[] }>('/training/ai/practice', {
        method: 'POST', body: JSON.stringify({ sourceResourceIds: [resource.id], count: 3 }),
      }, token);
      setPracticeQuestions(response.questions || []);
    } catch (requestError) {
      setPracticeQuestions([]);
      setError(requestError instanceof Error ? requestError.message : 'AI练习生成失败，请稍后重试。');
    } finally {
      setLoading(false);
    }
  }

  async function generate(action: 'qa' | 'summary' | 'suggestions', suggestedQuestion?: string) {
    const prompt = (suggestedQuestion ?? question).trim();
    if (loading || (action === 'qa' && !prompt)) return;
    if (action === 'qa') setQuestion(prompt);
    setLoading(true);
    setError('');
    setContent('');
    try {
      const body = action === 'qa'
        ? { sourceResourceIds: [resource.id], question: prompt }
        : { sourceResourceIds: [resource.id] };
      const response = await api<{ content: string }>(`/training/ai/${action}`, {
        method: 'POST', body: JSON.stringify(body),
      }, token);
      setContent(response.content);
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'AI助教暂时不可用，请稍后重试。');
    } finally {
      setLoading(false);
    }
  }

  function resetPractice() {
    setPracticeIndex(0);
    setPracticeAnswer(null);
    setCorrectCount(0);
  }

  function answerPractice(index: number) {
    if (practiceAnswer !== null) return;
    setPracticeAnswer(index);
    if (index === practiceQuestions[practiceIndex]?.answer) setCorrectCount((count) => count + 1);
  }

  function nextPractice() {
    if (practiceIndex < practiceQuestions.length - 1) {
      setPracticeIndex((index) => index + 1);
      setPracticeAnswer(null);
    }
  }

  const activeTool = tools.find((item) => item.id === tool)!;
  const practice = practiceQuestions[practiceIndex];
  const practiceFinished = practiceIndex === practiceQuestions.length - 1 && practiceAnswer !== null;

  return <div>
    <section className="overflow-hidden rounded-md bg-[#123f43] text-white shadow-sm">
      <div className="grid items-center gap-6 p-7 md:grid-cols-[1fr_240px] md:p-9"><div><p className="text-xs font-bold uppercase tracking-[0.18em] text-teal-200">Course AI tutor</p><h1 className="mt-3 text-3xl font-semibold">{resource.title} · AI助教</h1><p className="mt-3 max-w-2xl text-sm leading-7 text-teal-50/75">基于当前课程已发布资料提供问答、总结、学习建议和即时陪练。生成内容用于辅助学习，请结合课程原文核查重要信息。</p></div><img className="h-32 w-full rounded-md object-cover" src={resource.coverUrl || '/assets/default-course-cover.png'} alt="" /></div>
    </section>

    <section className="mt-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-4" aria-label="AI助教功能">{tools.map((item) => <button className={`flex min-h-32 items-start gap-4 rounded-md border bg-white p-5 text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-md ${tool === item.id ? 'border-teal-600 ring-2 ring-teal-100' : 'border-slate-200'}`} key={item.id} onClick={() => chooseTool(item.id)}><span className={`grid h-12 w-12 shrink-0 place-items-center rounded-md ${item.tone}`}><item.icon size={24} /></span><span><strong className="block">{item.title}</strong><small className="mt-2 block leading-5 text-slate-500">{item.description}</small></span></button>)}</section>

    <section className="mt-6 rounded-md bg-white p-6 shadow-sm sm:p-8">
      <div className="flex items-start gap-4 border-b border-slate-100 pb-6"><span className={`grid h-11 w-11 shrink-0 place-items-center rounded-md ${activeTool.tone}`}><activeTool.icon size={22} /></span><div><h2 className="text-xl font-semibold">{activeTool.title}</h2><p className="mt-1 text-sm text-slate-500">{activeTool.description}</p></div></div>

      {tool === 'qa' && <div className="mt-6"><p className="text-sm font-semibold text-slate-700">你可以这样问</p><div className="mt-3 grid gap-2 sm:grid-cols-2">{suggestedQuestions.map((item) => <button type="button" className="min-h-12 rounded-md border border-slate-200 bg-slate-50 px-4 py-3 text-left text-sm leading-6 text-slate-600 transition hover:border-teal-500 hover:bg-teal-50 hover:text-teal-800 disabled:cursor-not-allowed disabled:opacity-50" disabled={loading} key={item.question} onClick={() => askSuggestedQuestion(item)}>{item.question}</button>)}</div><label className="mt-6 block text-sm font-semibold text-slate-700" htmlFor="course-ai-question">向AI助教提问</label><textarea id="course-ai-question" className="mt-3 min-h-32 w-full resize-y rounded-md border border-slate-300 p-4 text-sm leading-6 outline-none focus:border-teal-600 focus:ring-4 focus:ring-teal-100" value={question} maxLength={500} onChange={(event) => setQuestion(event.target.value)} placeholder={`例如：${resource.title}中最需要注意的风险是什么？`} /><div className="mt-3 flex justify-end"><button className="inline-flex min-h-11 items-center gap-2 rounded-md bg-teal-700 px-5 text-sm font-semibold text-white disabled:opacity-50" disabled={loading || !question.trim()} onClick={() => generate('qa')}><Sparkles size={17} />{loading ? '正在思考…' : '发送问题'}</button></div></div>}
      {tool === 'summary' && <AiGenerateAction loading={loading} button="生成课程总结" description="AI助教将按照学习目标、核心要点、风险提示和记录要求整理当前课程。" onGenerate={() => generate('summary')} />}
      {tool === 'suggestions' && <AiGenerateAction loading={loading} button="生成学习建议" description="AI助教将结合本课程内容给出阅读、复习和练习顺序。" onGenerate={() => generate('suggestions')} />}
      {error && <p className="mt-6 rounded-md bg-red-50 p-4 text-sm text-red-700" role="alert">{error}</p>}
      {content && <div className="mt-6 border-l-4 border-teal-500 bg-teal-50 p-5"><div className="whitespace-pre-wrap text-sm leading-8 text-slate-700">{content}</div><p className="mt-4 text-xs text-slate-500">内容由AI根据当前课程资料生成，请核查重要信息。</p></div>}

      {tool === 'practice' && (practice ? <div className="mt-6"><div className="flex items-center justify-between"><span className="text-sm font-semibold text-teal-700">第 {practiceIndex + 1}/{practiceQuestions.length} 题</span><span className="text-sm text-slate-500">答对 {correctCount} 题</span></div><div className="mt-3 h-2 overflow-hidden rounded-full bg-slate-100"><span className="block h-full rounded-full bg-teal-500 transition-all" style={{ width: `${((practiceIndex + 1) / practiceQuestions.length) * 100}%` }} /></div><h3 className="mt-7 text-lg font-semibold leading-8">{practice.question}</h3><div className="mt-5 grid gap-3">{practice.options.map((option, index) => { const answered = practiceAnswer !== null; const correct = index === practice.answer; const selected = index === practiceAnswer; const style = answered && correct ? 'border-emerald-500 bg-emerald-50 text-emerald-800' : answered && selected ? 'border-red-400 bg-red-50 text-red-700' : selected ? 'border-teal-600 bg-teal-50' : 'border-slate-200 hover:border-teal-400'; return <button className={`min-h-14 rounded-md border px-5 text-left text-sm transition ${style}`} disabled={answered} key={option} onClick={() => answerPractice(index)}><span className="mr-3 font-semibold">{String.fromCharCode(65 + index)}.</span>{option}</button>; })}</div>{practiceAnswer !== null && <div className="mt-5 rounded-md bg-slate-50 p-5"><strong className={practiceAnswer === practice.answer ? 'text-emerald-700' : 'text-red-600'}>{practiceAnswer === practice.answer ? '回答正确' : '回答错误'}</strong><p className="mt-2 text-sm leading-7 text-slate-600">{practice.explanation}</p></div>}<div className="mt-6 flex justify-end">{practiceFinished ? <button className="inline-flex items-center gap-2 rounded-md bg-teal-700 px-5 py-3 text-sm font-semibold text-white" onClick={() => void loadPractice()}><RefreshCw size={17} />重新生成</button> : <button className="rounded-md bg-teal-700 px-6 py-3 text-sm font-semibold text-white disabled:opacity-40" disabled={practiceAnswer === null} onClick={nextPractice}>下一题</button>}</div></div> : <AiGenerateAction loading={loading} button="生成陪练题" description="AI助教将依据当前课程资料生成三道练习题。" onGenerate={() => void loadPractice()} />)}
    </section>
  </div>;
}

function AiGenerateAction({ loading, button, description, onGenerate }: { loading: boolean; button: string; description: string; onGenerate: () => void }) {
  return <div className="mt-6 flex flex-col items-start justify-between gap-5 rounded-md bg-slate-50 p-6 sm:flex-row sm:items-center"><p className="max-w-2xl text-sm leading-7 text-slate-600">{description}</p><button className="inline-flex min-h-11 shrink-0 items-center gap-2 rounded-md bg-teal-700 px-5 text-sm font-semibold text-white disabled:opacity-50" disabled={loading} onClick={onGenerate}><Sparkles size={17} />{loading ? '正在生成…' : button}</button></div>;
}

function courseSuggestedQuestions(resource: TrainingResource): SuggestedQuestion[] {
  if (resource.title.includes('跌倒')) {
    return [
      { question: '跌倒预防中需要重点观察哪些风险信号？', answer: '应重点观察老人近期是否有跌倒史，是否出现头晕、乏力、步态不稳、视力下降或意识状态变化，同时关注服药后反应、起身过快、鞋袜不合适以及助行器使用不当等情况。发现风险变化应及时记录并报告负责人。' },
      { question: '协助老人起身和行走时应遵循哪些步骤？', answer: '先确认环境安全并准备合适的鞋袜和助行器；协助老人缓慢坐起，在床边停留并询问是否头晕；确认状态稳定后再协助站立；行走时站在老人较弱一侧或后侧提供保护，保持通道畅通，不催促、不强拉，并根据风险等级安排陪护。' },
      { question: '老人发生跌倒后，护工应如何正确处理？', answer: '不要立即强行扶起老人。首先保持现场安全，观察意识、呼吸、疼痛、出血和肢体异常，立即呼叫专业人员并按机构流程报告；在专业人员到达前给予适当保护和安抚，不擅自搬动疑似骨折或头部受伤的老人；事后如实记录时间、地点、经过和观察结果。' },
      { question: '请根据课程资料整理一份环境安全检查清单。', answer: '环境检查可包括：地面干燥且无杂物；通道和床边照明充足；常用物品放在老人可及位置；床、轮椅和助行器稳定且制动有效；扶手牢固；电线不横跨通道；鞋袜防滑合脚；呼叫装置可用；卫生间配有防滑和扶手设施。发现问题应立即处理或上报。' },
    ];
  }
  if (resource.title.includes('压疮') || resource.title.includes('压力性损伤')) {
    return [
      { question: '哪些老人更容易发生压力性损伤？', answer: '长期卧床或活动受限、营养状况较差、大小便失禁、感觉障碍、皮肤潮湿、消瘦或水肿，以及不能自主变换体位的老人风险较高。护工应按护理计划重点观察，而不是自行作出医疗诊断。' },
      { question: '日常照护中应如何进行皮肤观察和体位变换？', answer: '按护理计划协助老人规律变换体位，动作轻柔，避免拖拉造成摩擦；重点查看骶尾部、足跟、髋部、肘部等受压部位是否发红、破损或潮湿；保持皮肤清洁干燥和床单位平整无碎屑，并记录每次观察及体位变化。' },
      { question: '发现受压部位持续发红时应该怎么处理？', answer: '应立即减轻该部位压力，停止摩擦或按摩，保持局部清洁干燥，并及时报告负责人或专业医护人员，同时记录发现时间、部位、颜色变化和皮肤完整情况。护工不得自行处理深部伤口或使用未经许可的药物。' },
      { question: '请根据课程资料整理一份压疮预防检查清单。', answer: '检查要点包括：是否按计划变换体位；受压部位皮肤是否完整；床单是否平整、干燥、无碎屑；衣物和纸尿裤是否潮湿；支撑垫位置是否正确；老人营养和饮水异常是否已报告；每次皮肤观察、体位变换和异常情况是否完成记录。' },
    ];
  }
  if (resource.title.includes('手卫生') || resource.title.includes('感染')) {
    return [
      { question: '手卫生的五个关键时刻分别是什么？', answer: '五个关键时刻是：接触老人之前；进行清洁或无菌相关操作之前；可能接触血液、分泌物、排泄物等体液之后；接触老人之后；接触老人周围环境和物品之后。' },
      { question: '什么情况下应洗手，什么情况下可以进行手消毒？', answer: '双手有肉眼可见污物、接触排泄物或机构明确要求时，应使用流动水和洗手液洗手。双手无可见污物且符合机构规范时，可使用速干手消毒剂。具体操作应遵循所在机构的感染控制制度。' },
      { question: '佩戴和摘除手套时需要注意哪些事项？', answer: '戴手套前先进行手卫生，选择合适尺寸并检查完整性；不同老人或不同操作之间要更换手套；避免戴手套触碰清洁公共区域；摘除时不要让污染面接触皮肤，摘后立即进行手卫生。手套不能替代手卫生。' },
      { question: '请根据课程资料总结感染预防的关键步骤。', answer: '感染预防应做到：在关键时刻规范手卫生；正确使用手套等防护用品；清洁与污染物品分开；及时处理体液污染；保持环境和常用物品清洁；发现发热、异常分泌物等情况及时报告；按要求完成清洁、消毒和操作记录。' },
    ];
  }
  return [
    { question: `请概括《${resource.title}》的核心要点。`, answer: `《${resource.title}》强调在护理操作前确认学习目标和服务对象状态，操作中遵循规范流程、观察风险信号，操作后完成清洁整理和记录。遇到异常情况应停止不安全操作并及时报告，不以个人经验替代机构制度和专业判断。` },
    { question: '这门课程中最需要注意的风险有哪些？', answer: '需要关注服务对象状态变化、环境安全、操作步骤遗漏、交叉感染、辅助用具使用不当以及记录不完整等风险。实施操作前应核对要求，过程中持续观察，发现异常及时停止并报告。' },
    { question: '实际护理操作时应该遵循哪些关键步骤？', answer: '一般应依次完成：确认任务与对象、评估环境和风险、准备用品并进行手卫生、解释并取得配合、按规范实施操作、持续观察反应、整理环境与用品、记录结果并报告异常。具体步骤以课程资料和机构制度为准。' },
    { question: '请根据课程资料整理一份复习清单。', answer: '复习时可依次核对：学习目标是否理解；操作前准备是否完整；关键步骤能否按顺序复述；主要风险信号是否掌握；异常情况是否知道如何报告；操作后的清洁和记录要求是否清楚；相关练习题是否完成并订正。' },
  ];
}

type ChapterMaterial = { title: string; summary: string; paragraphs: string[]; points: string[]; reminder: string };

function Chapters({ userId, resource, score, token }: { userId: number; resource: TrainingResource; score?: CourseScore; token: string }) {
  const [detail, setDetail] = useState<TrainingResource>(resource);
  const [activeChapter, setActiveChapter] = useState<number | null>(null);
  const storageKey = `carenexus-course-chapters:${userId}:${resource.id}`;
  const [completedChapters, setCompletedChapters] = useState<number[]>(() => {
    try { return JSON.parse(localStorage.getItem(storageKey) || '[]') as number[]; } catch { return []; }
  });
  const [saving, setSaving] = useState(false);
  useEffect(() => { api<TrainingResource>(`/training/resources/${resource.id}`, {}, token).then(setDetail).catch(() => undefined); }, [resource.id, token]);
  useEffect(() => {
    api<CourseLearningStatus>(`/training/learning/resources/${resource.id}`, {}, token).then((status) => {
      if (status.completed) {
        setCompletedChapters([0, 1, 2]);
        localStorage.setItem(storageKey, JSON.stringify([0, 1, 2]));
      }
    }).catch(() => undefined);
  }, [resource.id, storageKey, token]);
  const chapters = courseChapterMaterials(detail);

  async function completeChapter(index: number) {
    if (completedChapters.includes(index) || saving) return;
    setSaving(true);
    try {
      await api('/training/learning/access', {
        method: 'POST', body: JSON.stringify({ resourceId: resource.id, accessSeconds: 600 }),
      }, token);
      const next = [...completedChapters, index].sort();
      setCompletedChapters(next);
      localStorage.setItem(storageKey, JSON.stringify(next));
    } finally {
      setSaving(false);
    }
  }

  if (activeChapter !== null) {
    const chapter = chapters[activeChapter];
    const extendedSections = extendedChapterSections(detail, chapter);
    const sourceLinks = courseSourceLinks(detail);
    return <div>
      <button className="mb-5 inline-flex items-center gap-2 text-sm font-semibold text-slate-600 hover:text-teal-700" onClick={() => setActiveChapter(null)}><ArrowLeft size={17} />返回章节目录</button>
      <article className="overflow-hidden rounded-md bg-white shadow-sm">
        <div className="relative h-64 bg-cover bg-center" style={{ backgroundImage: `linear-gradient(90deg, rgba(7,45,48,.72), rgba(7,45,48,.18)), url('${detail.coverUrl || '/assets/default-course-cover.png'}')` }}><div className="absolute inset-0 flex flex-col justify-end p-8 text-white"><span className="text-sm font-semibold text-teal-100">第 {activeChapter + 1} 章 · 约 10 分钟</span><h1 className="mt-2 max-w-3xl text-3xl font-semibold">{chapter.title}</h1><p className="mt-3 max-w-2xl text-sm leading-6 text-white/80">{chapter.summary}</p></div></div>
        <div className="grid gap-8 p-7 lg:grid-cols-[minmax(0,1fr)_280px] lg:p-10"><div><div className="space-y-5 text-[15px] leading-8 text-slate-700">{chapter.paragraphs.map((paragraph) => <p key={paragraph}>{paragraph}</p>)}</div><h2 className="mt-9 text-xl font-semibold">本章要点</h2><ol className="mt-4 grid gap-3">{chapter.points.map((point, index) => <li className="flex gap-3 rounded-md bg-slate-50 px-4 py-3 text-sm leading-6" key={point}><span className="font-semibold text-teal-700">{index + 1}</span><span>{point}</span></li>)}</ol><div className="mt-10 space-y-9">{extendedSections.map((section) => <section key={section.title}><h2 className="text-xl font-semibold">{section.title}</h2><div className="mt-4 space-y-4 text-[15px] leading-8 text-slate-700">{section.paragraphs.map((paragraph) => <p key={paragraph}>{paragraph}</p>)}</div></section>)}</div><section className="mt-10 border-t border-slate-200 pt-7"><h2 className="text-lg font-semibold">资料依据</h2><ul className="mt-4 grid gap-2 text-sm leading-6">{sourceLinks.map((source) => <li key={source.url}><a className="text-teal-700 underline decoration-teal-200 underline-offset-4 hover:text-teal-900" href={source.url} target="_blank" rel="noreferrer">{source.label}</a></li>)}</ul></section></div><aside className="h-fit self-start border-l-4 border-amber-400 bg-amber-50 p-5"><strong className="text-sm">学习提示</strong><p className="mt-3 text-sm leading-7 text-slate-600">{chapter.reminder}</p><p className="mt-5 text-xs leading-6 text-slate-500">本资料用于基础护理培训，不替代医疗机构制度、专业医嘱或持证医护人员判断。</p></aside></div>
        <footer className="flex flex-wrap items-center justify-between gap-3 border-t border-slate-100 px-7 py-5 lg:px-10"><button className="rounded-md border border-slate-300 px-5 py-2.5 text-sm font-semibold disabled:opacity-40" disabled={activeChapter === 0} onClick={() => setActiveChapter(activeChapter - 1)}>上一章</button><button className={`rounded-md px-6 py-2.5 text-sm font-semibold ${completedChapters.includes(activeChapter) ? 'bg-emerald-50 text-emerald-700' : 'bg-teal-700 text-white'}`} disabled={saving || completedChapters.includes(activeChapter)} onClick={() => completeChapter(activeChapter)}>{completedChapters.includes(activeChapter) ? '本章已完成' : saving ? '正在记录…' : '完成本章'}</button><button className="rounded-md border border-slate-300 px-5 py-2.5 text-sm font-semibold disabled:opacity-40" disabled={activeChapter === chapters.length - 1} onClick={() => setActiveChapter(activeChapter + 1)}>下一章</button></footer>
      </article>
    </div>;
  }

  return <><PageTitle eyebrow="Course chapters" title="课程章节" description="按照课程资料组织学习内容，完成后进入作业与考试。" /><div className="rounded-md bg-white shadow-sm"><div className="flex items-center justify-between border-b border-slate-100 p-6"><div><strong>学习目录</strong><span className="ml-3 text-sm text-slate-400">{chapters.length} 个章节</span></div><span className={`text-sm font-semibold ${score?.passed || completedChapters.length === chapters.length ? 'text-emerald-600' : 'text-slate-500'}`}>{score?.passed ? '课程已通过' : completedChapters.length === chapters.length ? '资料已学完' : `学习中 · ${completedChapters.length}/${chapters.length}`}</span></div>{chapters.map((chapter, index) => <button className="flex w-full gap-4 border-b border-slate-100 p-6 text-left transition last:border-0 hover:bg-slate-50" key={chapter.title} onClick={() => setActiveChapter(index)}><span className={`grid h-9 w-9 shrink-0 place-items-center rounded-full text-sm font-semibold ${completedChapters.includes(index) ? 'bg-emerald-100 text-emerald-700' : 'bg-teal-50 text-teal-700'}`}>{completedChapters.includes(index) ? '✓' : index + 1}</span><span className="min-w-0 flex-1"><strong className="font-semibold">{chapter.title}</strong><span className="mt-2 block text-sm leading-6 text-slate-500">{chapter.summary}</span></span><span className="self-center text-sm font-semibold text-teal-700">查看资料 →</span></button>)}</div></>;
}

function extendedChapterSections(resource: TrainingResource, chapter: ChapterMaterial) {
  const keyPoints = chapter.points.join('、');
  return [
    {
      title: '岗位边界与学习目标',
      paragraphs: [
        `学习“${chapter.title}”时，首先要明确护工的职责是依据机构制度和已经确认的照护计划，完成生活照护、安全观察、过程记录和异常报告。护工需要理解本章所列的风险信号和操作要求，但不能据此自行作出疾病诊断、调整药物、处理专业伤口或开展其他超出岗位授权的医疗操作。遇到资料与本机构流程不一致时，应暂停自行判断，向护理负责人或医护人员确认后执行。`,
        `本章学习不能只停留在记住条目，还应能够在实际场景中说明“为什么这样做、何时需要停止、发现问题向谁报告”。完成学习后，应能复述并识别以下关键内容：${keyPoints}。同时要把保护老人尊严、尊重老人意愿和注意隐私贯穿整个照护过程，在解释操作时使用清楚、平和的语言，避免用命令、催促或责备的方式与老人沟通。`,
      ],
    },
    {
      title: '情境化操作训练',
      paragraphs: [
        `可以用“操作前、操作中、操作后”三个阶段练习本章内容。操作前先核对服务对象和本次任务，阅读交接信息，检查环境、用品及个人防护是否符合要求，并向老人说明接下来要做什么；操作中持续观察老人的表情、语言、动作和身体反应，按规定顺序完成步骤，不为了赶时间省略风险确认；操作后整理环境和用品，再次确认老人处于安全、舒适状态，最后完成记录和必要的交接。`,
        `情境练习时，可以假设老人表示不适、环境突然出现风险，或实际情况与交接信息不一致。此时不应机械地继续原计划，而要先保证老人安全，停止可能加重风险的动作，询问老人感受并呼叫有权限的人员。等待专业人员到场期间，护工应留在现场持续观察，不随意搬动老人，不向无关人员传播隐私信息，也不对老人或家属作出未经授权的诊断和结果承诺。`,
      ],
    },
    {
      title: '沟通、交接与记录要求',
      paragraphs: [
        `有效沟通是安全照护的一部分。开始操作前应向老人说明目的和大致步骤，确认老人理解并愿意配合；对听力、视力或认知能力受限的老人，应放慢语速、使用简短句子，并结合机构允许的辅助方式确认其反应。涉及身体暴露时应做好遮挡，只让当前任务需要的人员在场。老人拒绝或明显抗拒时，不强行执行，应记录情况并报告负责人。`,
        `记录应当及时、客观、可追溯，重点写清时间、地点、观察到的事实、完成的照护措施、老人反应、异常报告对象和后续安排。不要使用“情况还好”“应该没事”等模糊判断，也不要把推测写成事实。交接时应突出仍需继续观察的问题和已经采取的措施，接班人员确认后再结束交接。涉及老人身份、联系方式和健康情况的内容只能在授权范围内使用，不能拍照转发或带离工作系统。`,
      ],
    },
    {
      title: '常见错误与章节自查',
      paragraphs: [
        `常见错误包括：只凭经验跳过核对流程；看到老人暂时没有明显不适就忽略早期风险；为了节省时间同时处理多个任务而造成用品混用；发现异常后先自行处理、延迟报告；记录只写结果而缺少时间、观察事实和处置过程；以及把培训资料中的一般原则直接当成对某位老人的个体化医疗方案。以上做法都可能增加风险，也会使后续人员无法准确了解情况。`,
        `完成本章前请进行自查：我能否用自己的话说明本章风险和预防目的？能否按顺序说出操作前、中、后的关键动作？能否区分护工可以执行的基础照护与必须上报的专业问题？能否写出一条只包含客观事实的照护记录？如果其中任何一项还不确定，应返回本章要点再次阅读，并结合机构操作手册向负责人确认。确认掌握后再点击“完成本章”，进入下一章学习。`,
      ],
    },
  ];
}

function courseSourceLinks(resource: TrainingResource) {
  if (resource.title.includes('压疮')) return [
    { label: '《养老机构预防压疮服务规范》MZ/T 132—2019', url: 'https://www.gov.cn/xinwen/2019-12/24/5463665/files/102425322dc34f3895746a645264ab46.pdf' },
    { label: '《养老机构服务安全基本规范》GB 38600—2019', url: 'https://openstd.samr.gov.cn/bzgk/std/newGbInfo?hcno=D8AB02F0141FE11A4976E0E94FCF58B4' },
  ];
  if (resource.title.includes('跌倒')) return [
    { label: '《养老机构预防老年人跌倒基本规范》MZ/T 185—2021', url: 'https://std.samr.gov.cn/hb/search/stdHBDetailed?id=D7B674CDFE8DA05AE05397BE0A0A8835' },
    { label: '《养老机构服务安全基本规范》GB 38600—2019', url: 'https://openstd.samr.gov.cn/bzgk/std/newGbInfo?hcno=D8AB02F0141FE11A4976E0E94FCF58B4' },
  ];
  return [
    { label: '国家卫生健康委：手卫生五个关键时刻与手卫生措施', url: 'https://www.nhc.gov.cn/yzygj/c100068/202109/e4723e15329f4ac0a6b5ed6e7101e501.shtml' },
    { label: '世界卫生组织：Five moments for hand hygiene', url: 'https://www.who.int/publications/m/item/five-moments-for-hand-hygiene' },
  ];
}

function courseChapterMaterials(resource: TrainingResource): ChapterMaterial[] {
  if (resource.title.includes('压疮')) return [
    { title: '认识压力性损伤与风险观察', summary: '了解高风险人群、常见受压部位及早期异常信号。', paragraphs: ['压疮又称压力性损伤。长期卧床、久坐、活动受限或感觉减退的老年人风险较高，护工应按照机构评估结果和护理计划开展照护。', '翻身、清洁和交接班时，应重点观察骶尾部、足跟、髋部、肘部等容易受压的位置，同时检查纸尿裤、衣物和被褥是否干燥、平整。'], points: ['观察皮肤是否干燥、颜色改变或破损。', '重点检查骶尾部、足跟、髋部和肘部。', '发现持续发红、水疱、渗液、异味或疼痛立即报告。'], reminder: '不要自行处理已经出现的深部伤口，也不要用力按摩持续发红的受压部位。' },
    { title: '日常减压与皮肤照护', summary: '掌握体位变换、清洁干燥、床单位整理和减压器具使用。', paragraphs: ['应按照护理计划协助老年人变换体位，避免同一部位长时间持续受压。大小便污染后及时清洁，并保持皮肤干燥。', '床单应平整无褶皱，及时清除碎屑和硬物。减压床垫、坐垫等器具应按评估和机构要求使用，使用前检查完好性和位置。'], points: ['按护理计划变换体位。', '保持皮肤清洁干燥。', '保持床单位平整并清除碎屑。', '正确检查和使用减压器具。'], reminder: '体位变换频率和减压器具选择应遵循护理计划，不自行改变专业方案。' },
    { title: '异常报告与照护记录', summary: '学习发现异常后的处置边界和真实记录要求。', paragraphs: ['发现皮肤持续发红、破损、水疱、渗液、异味或明显疼痛时，应停止可能加重损伤的操作，立即报告护理负责人或医护人员。', '每次观察、翻身和异常情况都应如实记录，内容包括时间、部位、现象和已采取的报告措施，不可事后补写或虚构。'], points: ['先保护老人并避免继续受压。', '及时报告护理负责人或医护人员。', '记录观察时间、部位、异常表现和处理过程。'], reminder: '护工负责观察、基础照护、报告和记录，不进行伤口诊断或超出岗位授权的治疗操作。' },
  ];
  if (resource.title.includes('跌倒')) return [
    { title: '识别跌倒风险', summary: '认识高龄、步态不稳、视力下降和药物反应等常见风险。', paragraphs: ['老年人跌倒可能导致骨折、头部损伤和活动能力下降。高龄、步态不稳、视力下降、认知障碍、近期跌倒史以及服药后头晕乏力都需要重点关注。', '护工应依照机构的跌倒风险评估结果提供相应级别的陪护，不能仅凭个人感觉降低陪护等级。'], points: ['留意步态、视力、认知和近期跌倒史。', '观察服药后的嗜睡、头晕和乏力。', '按照风险评估结果提供陪护。'], reminder: '发现老人状态与平时不同，应先保证安全并及时报告，不催促其继续活动。' },
    { title: '环境整理与安全陪护', summary: '掌握环境预防、起身行走协助和助行器具使用原则。', paragraphs: ['居室、厕所、走廊和活动区域应保持地面干燥、通道无障碍物。清洁地面前和过程中放置明显警示标志，并检查夜间照明、扶手和呼叫设备。', '高风险老人起床、行走和如厕时应使用合适助行器具或由工作人员协助。先协助坐起，确认无头晕和乏力后再站立。'], points: ['保持地面干燥和通道畅通。', '湿滑区域设置安全警示。', '起身、行走和如厕按风险级别陪护。', '检查助行器具、照明和呼叫设备。'], reminder: '有助行器不等于可以无人陪护，实际陪护方式以风险评估和机构要求为准。' },
    { title: '跌倒后的应急处理', summary: '学习跌倒发生后的观察、呼叫、保护和记录流程。', paragraphs: ['跌倒发生后不要立即强行扶起。应先观察意识、呼吸、疼痛、出血和肢体异常，避免随意搬动可能受伤的部位。', '立即呼叫专业人员，按机构应急流程处理，并如实记录发生时间、地点、现场情况和报告处置过程。'], points: ['不要立即强行扶起。', '观察意识、呼吸、疼痛、出血和肢体异常。', '呼叫专业人员并保护现场安全。', '按流程完成报告和记录。'], reminder: '出现意识异常、明显出血或疑似骨折时，应立即启动机构应急流程。' },
  ];
  return [
    { title: '手卫生的作用与五个时刻', summary: '理解手卫生如何阻断传播，并掌握照护工作的五个关键时刻。', paragraphs: ['手卫生是阻断病原体通过双手传播的重要措施。护工接触不同老人、处理排泄物、协助进食、清洁环境或接触护理用品时，都应执行机构感染防控制度。', '五个关键时刻是：接触老人之前、清洁或无菌相关操作之前、可能接触体液之后、接触老人之后、接触老人周围环境和物品之后。'], points: ['接触老人之前。', '清洁或无菌相关操作之前。', '可能接触血液、分泌物或排泄物之后。', '接触老人之后。', '接触老人周围环境和物品之后。'], reminder: '床栏、床头柜、轮椅、便器和呼叫器都属于老人周围环境，接触后同样需要手卫生。' },
    { title: '洗手与手消毒方法选择', summary: '根据双手污染情况选择流动水洗手或速干手消毒剂。', paragraphs: ['双手有可见污物时，应使用流动水和洗手液洗手。双手无可见污物且机构允许时，可按要求使用速干手消毒剂。', '指甲应保持短而清洁，工作时避免佩戴可能藏污或划伤老人的饰物。手部有破损时，应按机构要求报告并采取防护措施。'], points: ['可见污物使用流动水和洗手液。', '无可见污物时按机构要求使用速干手消毒剂。', '保持指甲短而清洁，避免佩戴饰物。', '手部破损及时报告并防护。'], reminder: '具体揉搓步骤和作用时间应遵循机构张贴的手卫生操作规范。' },
    { title: '手套使用与常见误区', summary: '明确手套不能替代手卫生，并纠正常见错误做法。', paragraphs: ['佩戴手套不能替代手卫生。戴手套前应根据操作要求进行手卫生，手套污染、破损或从污染部位转向清洁部位时应及时更换。', '摘除手套后仍需进行手卫生，不得戴同一副手套接触不同老人或处理多个不同操作。'], points: ['手套不能替代洗手或手消毒。', '手套污染或破损后及时更换。', '摘除手套后仍要进行手卫生。', '不得用同一副手套跨老人或跨操作使用。'], reminder: '护工不得实施超出岗位授权的无菌操作或专业医疗操作。' },
  ];
}

function Discussions({ resourceId, token }: { resourceId: number; token: string }) {
  const [topics, setTopics] = useState<Discussion[]>([]); const [active, setActive] = useState<Discussion | null>(null);
  const [replies, setReplies] = useState<DiscussionReply[]>([]); const [showForm, setShowForm] = useState(false);
  const [replySending, setReplySending] = useState(false);
  const [title, setTitle] = useState(''); const [content, setContent] = useState(''); const [reply, setReply] = useState(''); const [error, setError] = useState('');
  const load = () => api<Discussion[]>(`/training/resources/${resourceId}/discussions`, {}, token).then(setTopics).catch((e) => setError(e.message));
  useEffect(() => { void load(); }, [resourceId, token]);
  async function createTopic(event: FormEvent) { event.preventDefault(); await api(`/training/resources/${resourceId}/discussions`, { method: 'POST', body: JSON.stringify({ title, content }) }, token); setTitle(''); setContent(''); setShowForm(false); load(); }
  async function openTopic(topic: Discussion) { setActive(topic); setReplies(await api<DiscussionReply[]>(`/training/discussions/${topic.id}/replies`, {}, token)); }
  async function sendReply(event: FormEvent) {
    event.preventDefault();
    const content = reply.trim();
    if (!active || !content || replySending) return;
    setError('');
    setReplySending(true);
    try {
      await api(`/training/discussions/${active.id}/replies`, {
        method: 'POST',
        body: JSON.stringify({ content }),
      }, token);
      setReply('');
      await openTopic(active);
      await load();
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : '回复发送失败，请稍后重试');
    } finally {
      setReplySending(false);
    }
  }
  return <><PageTitle eyebrow="Course discussion" title="课程讨论" description="围绕课程内容发起话题，与其他学习者交流。" /><div className="grid gap-5 lg:grid-cols-[1fr_320px]"><section className="rounded-md bg-white p-6 shadow-sm"><div className="mb-5 flex items-center justify-between"><strong>全部话题</strong><button className="rounded-md bg-teal-600 px-4 py-2 text-sm font-semibold text-white" onClick={() => setShowForm(!showForm)}>+ 新建话题</button></div>{showForm && <form className="mb-6 grid gap-3 rounded-md bg-slate-50 p-4" onSubmit={createTopic}><input className="rounded-md border p-3 text-sm" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="话题标题" required /><textarea className="min-h-24 rounded-md border p-3 text-sm" value={content} onChange={(e) => setContent(e.target.value)} placeholder="分享你的问题或看法" required /><button className="justify-self-end rounded-md bg-teal-600 px-4 py-2 text-sm text-white">发布</button></form>}{error && <p className="text-sm text-red-600" role="alert">{error}</p>}{topics.map((topic) => <button className="block w-full border-b border-slate-100 py-5 text-left last:border-0" key={topic.id} onClick={() => openTopic(topic)}><div className="flex justify-between gap-4"><strong>{topic.title}</strong><span className="text-xs text-slate-400">{topic.replyCount} 回复</span></div><p className="mt-2 line-clamp-2 text-sm text-slate-500">{topic.content}</p><small className="mt-3 block text-slate-400">{topic.authorName} · {new Date(topic.createdAt).toLocaleString('zh-CN')}</small></button>)}{topics.length === 0 && <p className="py-16 text-center text-sm text-slate-400">暂无讨论，欢迎发布第一个话题</p>}</section><aside className="rounded-md bg-white p-5 shadow-sm"><h2 className="font-semibold">{active?.title || '话题回复'}</h2>{active ? <><p className="mt-3 text-sm leading-6 text-slate-600">{active.content}</p><div className="mt-5 grid gap-3">{replies.map((item) => <div className="rounded-md bg-slate-50 p-3" key={item.id}><p className="text-sm">{item.content}</p><small className="mt-2 block text-slate-400">{item.authorName}</small></div>)}</div><form className="mt-5 grid gap-2" onSubmit={sendReply}><div className="flex gap-2"><input className="min-w-0 flex-1 rounded-md border px-3 text-sm" value={reply} onChange={(e) => setReply(e.target.value)} placeholder="写下回复" disabled={replySending} required /><button className="grid h-10 w-10 place-items-center rounded-md bg-teal-600 text-white disabled:cursor-not-allowed disabled:opacity-60" disabled={replySending || !reply.trim()} title={replySending ? '正在发送' : '发送回复'}><Send size={17} /></button></div>{error && <p className="text-sm text-red-600" role="alert">{error}</p>}</form></> : <p className="mt-4 text-sm text-slate-400">选择左侧话题查看并回复</p>}</aside></div></>;
}

function SocialDiscussions({ resourceId, token }: { resourceId: number; token: string }) {
  const [topics, setTopics] = useState<Discussion[]>([]);
  const [expandedId, setExpandedId] = useState<string | null>(null);
  const [replies, setReplies] = useState<Record<string, DiscussionReply[]>>({});
  const [sort, setSort] = useState<'LATEST' | 'HOT'>('LATEST');
  const [showComposer, setShowComposer] = useState(false);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [replyText, setReplyText] = useState('');
  const [replyTarget, setReplyTarget] = useState<DiscussionReply | null>(null);
  const [pending, setPending] = useState(false);
  const [error, setError] = useState('');

  const loadTopics = async () => {
    try {
      setTopics(await api<Discussion[]>(`/training/resources/${resourceId}/discussions?sort=${sort}`, {}, token));
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : '讨论加载失败');
    }
  };

  const loadReplies = async (discussionId: string) => {
    const items = await api<DiscussionReply[]>(`/training/discussions/${discussionId}/replies`, {}, token);
    setReplies((current) => ({ ...current, [discussionId]: items }));
  };

  useEffect(() => { void loadTopics(); }, [resourceId, token, sort]);

  async function createTopic(event: FormEvent) {
    event.preventDefault();
    if (!title.trim() || !content.trim() || pending) return;
    setPending(true); setError('');
    try {
      await api(`/training/resources/${resourceId}/discussions`, {
        method: 'POST', body: JSON.stringify({ title: title.trim(), content: content.trim() }),
      }, token);
      setTitle(''); setContent(''); setShowComposer(false);
      await loadTopics();
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : '发布失败');
    } finally { setPending(false); }
  }

  async function toggleComments(topic: Discussion) {
    if (expandedId === topic.id) { setExpandedId(null); return; }
    setExpandedId(topic.id); setReplyTarget(null); setReplyText('');
    try { await loadReplies(topic.id); }
    catch (requestError) { setError(requestError instanceof Error ? requestError.message : '评论加载失败'); }
  }

  async function submitReply(event: FormEvent, topic: Discussion) {
    event.preventDefault();
    if (!replyText.trim() || pending) return;
    setPending(true); setError('');
    try {
      await api(`/training/discussions/${topic.id}/replies`, {
        method: 'POST',
        body: JSON.stringify({ content: replyText.trim(), parentReplyId: replyTarget?.id || null }),
      }, token);
      setReplyText(''); setReplyTarget(null);
      await Promise.all([loadReplies(topic.id), loadTopics()]);
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : '评论发送失败');
    } finally { setPending(false); }
  }

  async function likeTopic(topic: Discussion) {
    await api(`/training/discussions/${topic.id}/like`, { method: 'PUT' }, token);
    await loadTopics();
  }

  async function likeReply(topicId: string, item: DiscussionReply) {
    await api(`/training/discussion-replies/${item.id}/like`, { method: 'PUT' }, token);
    await loadReplies(topicId);
  }

  async function deleteTopic(topic: Discussion) {
    if (!window.confirm('确定删除这条讨论及其全部评论吗？')) return;
    await api(`/training/discussions/${topic.id}`, { method: 'DELETE' }, token);
    if (expandedId === topic.id) setExpandedId(null);
    await loadTopics();
  }

  async function deleteReply(topicId: string, item: DiscussionReply) {
    if (!window.confirm('确定删除这条评论吗？')) return;
    await api(`/training/discussion-replies/${item.id}`, { method: 'DELETE' }, token);
    await Promise.all([loadReplies(topicId), loadTopics()]);
  }

  return <div className="mx-auto max-w-5xl">
    <PageTitle eyebrow="Course discussion" title="课程讨论" description="分享学习体会、提出问题，与其他学习者交流。" />
    <section className="mb-5 rounded-md bg-white p-5 shadow-sm">
      {!showComposer ? <button className="flex min-h-12 w-full items-center rounded-md bg-slate-50 px-4 text-left text-sm text-slate-500 hover:bg-slate-100" onClick={() => setShowComposer(true)}>有什么想和大家讨论的？</button>
        : <form className="grid gap-3" onSubmit={createTopic}><input className="rounded-md border border-slate-200 px-4 py-3 font-semibold outline-none focus:border-teal-600" value={title} onChange={(event) => setTitle(event.target.value)} placeholder="讨论标题" maxLength={160} required /><textarea className="min-h-28 resize-y rounded-md border border-slate-200 p-4 text-sm leading-6 outline-none focus:border-teal-600" value={content} onChange={(event) => setContent(event.target.value)} placeholder="写下你的问题、经验或学习体会" maxLength={3000} required /><div className="flex justify-end gap-2"><button type="button" className="rounded-md px-4 py-2 text-sm text-slate-500" onClick={() => setShowComposer(false)}>取消</button><button className="rounded-md bg-teal-700 px-5 py-2 text-sm font-semibold text-white disabled:opacity-50" disabled={pending}>发布讨论</button></div></form>}
    </section>
    <div className="mb-4 flex items-center justify-between"><strong className="text-sm text-slate-700">全部讨论 · {topics.length}</strong><div className="flex rounded-md bg-white p-1 shadow-sm"><button className={`rounded px-3 py-1.5 text-sm ${sort === 'LATEST' ? 'bg-teal-50 font-semibold text-teal-700' : 'text-slate-500'}`} onClick={() => setSort('LATEST')}>最新</button><button className={`rounded px-3 py-1.5 text-sm ${sort === 'HOT' ? 'bg-teal-50 font-semibold text-teal-700' : 'text-slate-500'}`} onClick={() => setSort('HOT')}>最热</button></div></div>
    {error && <p className="mb-4 rounded-md bg-red-50 px-4 py-3 text-sm text-red-600" role="alert">{error}</p>}
    <div className="grid gap-4">{topics.map((topic) => {
      const topicReplies = replies[topic.id] || [];
      return <article className="rounded-md bg-white p-5 shadow-sm sm:p-6" key={topic.id}>
        <header className="flex items-start gap-3"><span className="grid h-10 w-10 shrink-0 place-items-center rounded-full bg-teal-100 font-bold text-teal-800">{topic.authorName.slice(0, 1)}</span><div className="min-w-0 flex-1"><div className="flex items-start justify-between gap-3"><div><strong>{topic.authorName}</strong><time className="ml-2 text-xs text-slate-400">{new Date(topic.createdAt).toLocaleString('zh-CN')}</time></div>{topic.ownedByCurrentUser && <button className="text-slate-400 hover:text-red-600" title="删除讨论" onClick={() => deleteTopic(topic)}><Trash2 size={17} /></button>}</div><h2 className="mt-3 text-lg font-bold text-slate-900">{topic.title}</h2><p className="mt-2 whitespace-pre-wrap text-sm leading-7 text-slate-600">{topic.content}</p></div></header>
        <footer className="mt-4 flex gap-5 border-t border-slate-100 pt-3 pl-13 text-sm"><button className={`flex items-center gap-1.5 ${topic.liked ? 'font-semibold text-rose-600' : 'text-slate-500'}`} onClick={() => likeTopic(topic)}><Heart size={18} fill={topic.liked ? 'currentColor' : 'none'} />{topic.likeCount || '点赞'}</button><button className="flex items-center gap-1.5 text-slate-500" onClick={() => toggleComments(topic)}><MessageCircle size={18} />{topic.replyCount || '评论'}</button></footer>
        {expandedId === topic.id && <section className="mt-4 border-t border-slate-100 pt-4 sm:ml-13"><div className="grid gap-4">{topicReplies.map((item) => <div className="flex gap-3" key={item.id}><span className="grid h-8 w-8 shrink-0 place-items-center rounded-full bg-slate-100 text-xs font-bold text-slate-600">{item.authorName.slice(0, 1)}</span><div className="min-w-0 flex-1 rounded-md bg-slate-50 px-4 py-3"><div className="flex justify-between gap-3"><p className="text-sm"><strong>{item.authorName}</strong>{item.replyToAuthorName && <span className="font-normal text-slate-400"> 回复 {item.replyToAuthorName}</span>}</p>{item.ownedByCurrentUser && <button className="text-slate-400 hover:text-red-600" title="删除评论" onClick={() => deleteReply(topic.id, item)}><Trash2 size={15} /></button>}</div><p className="mt-1 whitespace-pre-wrap text-sm leading-6 text-slate-700">{item.content}</p><div className="mt-2 flex gap-4 text-xs"><button className={item.liked ? 'text-rose-600' : 'text-slate-400'} onClick={() => likeReply(topic.id, item)}><Heart className="mr-1 inline" size={14} fill={item.liked ? 'currentColor' : 'none'} />{item.likeCount || '点赞'}</button><button className="text-slate-400" onClick={() => { setReplyTarget(item); setReplyText(''); }}><Reply className="mr-1 inline" size={14} />回复</button></div></div></div>)}</div>{topicReplies.length === 0 && <p className="py-4 text-center text-sm text-slate-400">还没有评论，来发表第一条吧</p>}
          <form className="mt-4" onSubmit={(event) => submitReply(event, topic)}>{replyTarget && <div className="mb-2 flex items-center justify-between rounded bg-teal-50 px-3 py-2 text-xs text-teal-700"><span>回复 {replyTarget.authorName}</span><button type="button" onClick={() => setReplyTarget(null)}>取消</button></div>}<div className="flex gap-2"><input className="min-w-0 flex-1 rounded-md border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-teal-600" value={replyText} onChange={(event) => setReplyText(event.target.value)} placeholder={replyTarget ? `回复 ${replyTarget.authorName}` : '写下评论'} maxLength={3000} required /><button className="grid h-10 w-10 shrink-0 place-items-center rounded-md bg-teal-700 text-white disabled:opacity-50" disabled={pending || !replyText.trim()} title="发送评论"><Send size={17} /></button></div></form>
        </section>}
      </article>;
    })}{topics.length === 0 && <div className="rounded-md bg-white py-20 text-center text-sm text-slate-400 shadow-sm">暂无讨论，发布第一条学习动态吧</div>}</div>
  </div>;
}

function Assignments({ resource, token }: { resource: TrainingResource; token: string }) {
  const [items, setItems] = useState<Assignment[]>([]);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [selectedOptionIndexes, setSelectedOptionIndexes] = useState<Record<number, number>>({});
  const [filter, setFilter] = useState<ListFilter>('all');
  const [answering, setAnswering] = useState(false);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [reviewMode, setReviewMode] = useState(false);
  const [aiExplanations, setAiExplanations] = useState<Record<number, string>>({});
  const [explanationLoading, setExplanationLoading] = useState(false);

  async function load() {
    const loaded = await api<Assignment[]>(`/training/resources/${resource.id}/assignments`, {}, token);
    setItems(loaded);
    return loaded;
  }
  useEffect(() => { void load(); }, [resource.id, token]);

  const completed = items.length > 0 && items.every((item) => Boolean(item.submissionStatus));
  const gradedScores = items.map((item) => item.score).filter((score): score is number => score != null);
  const pendingManualReview = completed && gradedScores.length < items.length;
  const totalScore = gradedScores.length === items.length && items.length > 0
    ? Math.round(gradedScores.reduce((sum, score) => sum + Number(score), 0) / items.length)
    : null;
  const visible = filter === 'all' || (filter === 'completed' ? completed : !completed);
  const allAnswered = items.length > 0 && items.every((item) => (answers[item.id] ?? item.submittedAnswer ?? '').trim());
  const current = items[currentIndex];

  useEffect(() => {
    if (!reviewMode || !current || current.type === 'TEXT' || aiExplanations[current.id]) return;
    let active = true;
    setExplanationLoading(true);
    api<{ content: string }>('/training/ai/assignment-explanation', {
      method: 'POST',
      body: JSON.stringify({
        sourceResourceIds: [resource.id], question: current.content,
        userAnswer: answers[current.id] ?? current.submittedAnswer ?? '',
        standardAnswer: current.correctAnswer || '',
      }),
    }, token).then((response) => {
      if (active) setAiExplanations((values) => ({ ...values, [current.id]: response.content }));
    }).catch((requestError) => {
      if (active) setAiExplanations((values) => ({ ...values, [current.id]: requestError instanceof Error ? requestError.message : 'AI讲解暂时不可用' }));
    }).finally(() => { if (active) setExplanationLoading(false); });
    return () => { active = false; };
  }, [reviewMode, current?.id, token, resource.id]);

  function startAnswering() {
    setAnswers(Object.fromEntries(items.map((item) => [item.id, item.submittedAnswer || ''])));
    setSelectedOptionIndexes(Object.fromEntries(items.flatMap((item) => {
      if (item.type === 'TEXT' || !item.submittedAnswer) return [];
      if (item.type === 'TRUE_FALSE') return [[item.id, item.submittedAnswer === 'true' ? 0 : 1]];
      try {
        const itemOptions = JSON.parse(item.optionsJson || '[]') as string[];
        const selectedIndex = itemOptions.indexOf(item.submittedAnswer);
        return selectedIndex >= 0 ? [[item.id, selectedIndex]] : [];
      } catch {
        return [];
      }
    })));
    setCurrentIndex(0);
    setReviewMode(false);
    setAnswering(true);
  }

  async function submitPaper() {
    if (!allAnswered || submitting) return;
    setSubmitting(true);
    try {
      const results = await Promise.all(items.map((item) => api<Assignment>(`/training/assignments/${item.id}/submission`, {
        method: 'POST', body: JSON.stringify({ answer: answers[item.id] }),
      }, token)));
      setItems(results);
      setConfirmOpen(false);
      setReviewMode(true);
      const firstIncorrect = results.findIndex((item) => item.score != null && Number(item.score) < 60);
      setCurrentIndex(firstIncorrect >= 0 ? firstIncorrect : 0);
    } finally {
      setSubmitting(false);
    }
  }

  if (!answering) return <><PageTitle eyebrow="Course assignments" title="课程作业" description="进入作业后逐题完成，已提交作业可以无限重做。" /><FilterBar value={filter} onChange={setFilter} /><div className="rounded-md bg-white shadow-sm">{visible && items.length > 0 ? <button className="flex w-full items-center gap-5 px-6 py-6 text-left hover:bg-slate-50" onClick={startAnswering}><span className="grid h-14 w-14 shrink-0 place-items-center rounded-md bg-slate-200 text-sm font-semibold text-slate-600">作业</span><span className="min-w-0 flex-1"><strong className="block truncate">{resource.title}课后作业</strong><small className={`mt-2 block font-medium ${!completed ? 'text-slate-400' : pendingManualReview ? 'text-amber-600' : (totalScore || 0) >= 60 ? 'text-emerald-600' : 'text-red-600'}`}>{!completed ? `未完成 · 共${items.length}题` : pendingManualReview ? '已提交 · 待批改' : `已完成 · ${totalScore}分`}</small></span><span className="text-sm font-semibold text-teal-700">{completed ? '重新作答' : '进入作答'} →</span></button> : <p className="py-20 text-center text-sm text-slate-400">{items.length === 0 ? '作业暂未发布' : '当前筛选下暂无作业'}</p>}</div></>;

  if (!current) return null;
  let options: string[] = [];
  try { options = JSON.parse(current.optionsJson || '[]'); } catch { options = []; }
  const currentAnswer = answers[current.id] ?? current.submittedAnswer ?? '';
  const correctValue = current.type === 'TRUE_FALSE'
    ? (current.correctAnswer === '正确' || current.correctAnswer === 'true' ? 'true' : 'false')
    : current.correctAnswer;
  const reviewScore = items.every((item) => item.score != null)
    ? Math.round(items.reduce((sum, item) => sum + Number(item.score), 0) / items.length)
    : null;

  return <>
    <button className="mb-5 inline-flex items-center gap-2 text-sm font-semibold text-slate-600" onClick={() => setAnswering(false)}><ArrowLeft size={17} />返回作业列表</button>
    <PageTitle eyebrow="Assignment" title={`${resource.title}课后作业`} description={reviewMode ? `批改完成，本次成绩 ${reviewScore ?? 0} 分。切换题目可查看答案与讲解。` : `共 ${items.length} 题，完成全部题目后提交，客观题将自动评分。`} />
    <div className="grid items-start gap-5 lg:grid-cols-[minmax(0,1fr)_240px]">
      <article className="rounded-md bg-white p-7 shadow-sm">
        <div className="mb-6 flex items-center justify-between border-b border-slate-100 pb-5"><strong>第 {currentIndex + 1} 题</strong><span className="text-sm text-slate-400">{current.type === 'TEXT' ? '文本题' : current.type === 'TRUE_FALSE' ? '判断题' : '选择题'}</span></div>
        <p className="leading-7 text-slate-700">{current.content}</p>
        <div className="mt-6">{current.type === 'TEXT' ? <textarea className="min-h-40 w-full rounded-md border p-4 text-sm" value={currentAnswer} disabled={reviewMode} onChange={(event) => setAnswers({ ...answers, [current.id]: event.target.value })} placeholder="输入作业内容" /> : <div className="grid gap-3">{options.map((option, index) => { const value = current.type === 'TRUE_FALSE' ? (index === 0 ? 'true' : 'false') : option; const selected = selectedOptionIndexes[current.id] === index; const isCorrect = reviewMode && value === correctValue; const isWrongSelection = reviewMode && selected && value !== correctValue; const optionStyle = isCorrect ? 'border-emerald-500 bg-emerald-50 text-emerald-800' : isWrongSelection ? 'border-red-400 bg-red-50 text-red-700' : selected ? 'border-teal-600 bg-teal-50' : 'hover:border-slate-400'; return <label className={`flex items-center gap-3 rounded-md border p-4 text-sm transition ${reviewMode ? 'cursor-default' : 'cursor-pointer'} ${optionStyle}`} key={`${current.id}-${index}`}><input type="radio" name={`assignment-${current.id}`} checked={selected} disabled={reviewMode} onChange={() => { setAnswers({ ...answers, [current.id]: value }); setSelectedOptionIndexes({ ...selectedOptionIndexes, [current.id]: index }); }} />{option}{isCorrect && <span className="ml-auto text-xs font-semibold">正确答案</span>}{isWrongSelection && <span className="ml-auto text-xs font-semibold">你的选择</span>}</label>; })}</div>}</div>
        {reviewMode && current.type !== 'TEXT' && <div className={`mt-6 rounded-md border-l-4 p-5 ${Number(current.score) >= 60 ? 'border-emerald-500 bg-emerald-50' : 'border-amber-500 bg-amber-50'}`}><strong className="text-sm">AI 讲解</strong><p className="mt-2 text-sm leading-7 text-slate-700">{explanationLoading ? '正在生成讲解…' : aiExplanations[current.id] || '讲解准备中…'}</p><small className="mt-3 block text-slate-500">讲解由AI根据学习资料生成，请核查重要信息。</small></div>}
        <div className="mt-8 flex items-center justify-between border-t border-slate-100 pt-5"><button className="rounded-md border border-slate-300 px-5 py-2.5 text-sm font-semibold disabled:opacity-40" disabled={currentIndex === 0} onClick={() => setCurrentIndex(currentIndex - 1)}>上一题</button>{reviewMode ? <div className="flex gap-3"><button className="rounded-md border border-slate-300 px-5 py-2.5 text-sm font-semibold" onClick={() => setAnswering(false)}>返回作业列表</button><button className="rounded-md bg-teal-700 px-5 py-2.5 text-sm font-semibold text-white" onClick={startAnswering}>重新作答</button></div> : currentIndex < items.length - 1 ? <button className="rounded-md bg-teal-600 px-6 py-2.5 text-sm font-semibold text-white disabled:opacity-40" disabled={!currentAnswer.trim()} onClick={() => setCurrentIndex(currentIndex + 1)}>下一题</button> : <button className="rounded-md bg-teal-600 px-6 py-2.5 text-sm font-semibold text-white disabled:opacity-40" disabled={!allAnswered} onClick={() => setConfirmOpen(true)}>提交作业</button>}</div>
      </article>

      <aside className="sticky top-24 rounded-md bg-white p-5 shadow-sm"><h2 className="font-semibold">答题卡</h2><p className="mt-1 text-xs text-slate-400">{reviewMode ? `批改完成 · ${reviewScore ?? 0}分` : `已答 ${items.filter((item) => (answers[item.id] || '').trim()).length}/${items.length} 题`}</p><div className="mt-5 grid grid-cols-5 gap-2">{items.map((item, index) => { const answered = Boolean((answers[item.id] || '').trim()); const resultStyle = reviewMode ? Number(item.score) >= 60 ? 'border-emerald-400 bg-emerald-50 text-emerald-700' : 'border-red-400 bg-red-50 text-red-700' : answered ? 'border-teal-200 bg-teal-50 text-teal-700' : 'border-slate-200 text-slate-400'; return <button className={`grid aspect-square place-items-center rounded-md border text-sm font-semibold ${resultStyle} ${currentIndex === index ? 'ring-2 ring-slate-300' : ''}`} key={item.id} onClick={() => setCurrentIndex(index)}>{index + 1}</button>; })}</div>{reviewMode ? <button className="mt-6 min-h-10 w-full rounded-md border border-slate-300 text-sm font-semibold" onClick={() => setAnswering(false)}>返回作业列表</button> : <><button className="mt-6 min-h-10 w-full rounded-md bg-teal-700 text-sm font-semibold text-white disabled:opacity-40" disabled={!allAnswered} onClick={() => setConfirmOpen(true)}>确认提交</button>{!allAnswered && <p className="mt-3 text-center text-xs text-slate-400">完成全部题目后可提交</p>}</>}</aside>
    </div>

    {confirmOpen && <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/45 p-5" role="dialog" aria-modal="true" aria-labelledby="assignment-submit-title"><div className="w-full max-w-md rounded-lg bg-white p-7 shadow-2xl"><h2 className="text-xl font-semibold" id="assignment-submit-title">是否确认交卷？</h2><p className="mt-3 text-sm leading-6 text-slate-500">提交后将立即批改客观题并返回作业列表。你之后仍可重新作答。</p><div className="mt-7 flex justify-end gap-3"><button className="rounded-md border border-slate-300 px-5 py-2.5 text-sm font-semibold" disabled={submitting} onClick={() => setConfirmOpen(false)}>继续检查</button><button className="rounded-md bg-teal-700 px-5 py-2.5 text-sm font-semibold text-white disabled:opacity-50" disabled={submitting} onClick={submitPaper}>{submitting ? '正在交卷…' : '确认交卷'}</button></div></div></div>}
  </>;
}

function ExamPanel({ resourceId, score, token, onUpdated }: { resourceId: number; score?: CourseScore; token: string; onUpdated: () => void }) {
  const [exam, setExam] = useState<Exam | null>(null); const [answers, setAnswers] = useState<Record<number, string>>({}); const [result, setResult] = useState('');
  const [learning, setLearning] = useState<CourseLearningStatus | null>(null); const [selected, setSelected] = useState(false); const [filter, setFilter] = useState<ListFilter>('all');
  useEffect(() => { if (score?.examId) api<Exam>(`/training/exams/${score.examId}`, {}, token).then(setExam).catch(() => undefined); }, [score?.examId, token]);
  useEffect(() => { api<CourseLearningStatus>(`/training/learning/resources/${resourceId}`, {}, token).then(setLearning); }, [resourceId, token]);
  async function submit() { if (!exam) return; const payload = { answers: exam.questions.map((item) => ({ questionId: item.questionId, answer: answers[item.questionId] || '' })) }; const record = await api<{ score: number; passStatus: string }>(`/training/exams/${exam.id}/records`, { method: 'POST', body: JSON.stringify(payload) }, token); setResult(`本次成绩 ${record.score} 分，${record.passStatus === 'PASSED' ? '考试通过' : '未达到60分'}`); window.setTimeout(onUpdated, 1200); }
  const completed = Boolean(score?.passed); const visible = filter === 'all' || (filter === 'completed' ? completed : !completed);
  if (!selected) return <><PageTitle eyebrow="Course exam" title="课程考试" description="完成本课程全部学习内容后解锁考试，重考次数由管理员设置。" /><FilterBar value={filter} onChange={setFilter} /><div className="rounded-md bg-white shadow-sm">{exam && visible ? <button className="flex w-full items-center gap-5 px-6 py-6 text-left hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60" disabled={!learning?.completed} onClick={() => setSelected(true)}><span className="grid h-14 w-14 shrink-0 place-items-center rounded-md bg-slate-300 font-semibold text-white">考试</span><span className="min-w-0 flex-1"><strong className="block truncate">{exam.examName}</strong><small className={`mt-2 block ${completed ? 'text-emerald-600' : 'text-slate-400'}`}>{completed ? `已完成 · 最高${score?.bestScore}分` : learning?.completed ? '未完成' : '完成全部学习后解锁'}</small></span><span className="text-sm text-slate-500">最多作答 {exam.maxAttempts} 次</span></button> : <p className="py-20 text-center text-sm text-slate-400">{exam ? '当前筛选下暂无考试' : '考试暂未发布'}</p>}</div></>;
  if (!exam) return null;
  return <><button className="mb-5 inline-flex items-center gap-2 text-sm font-semibold text-slate-600" onClick={() => setSelected(false)}><ArrowLeft size={17} />返回考试列表</button><PageTitle eyebrow="Course exam" title={exam.examName} description={`及格分 ${exam.passScore} 分，最多作答 ${exam.maxAttempts} 次，当前最高分 ${score?.bestScore || 0} 分。`} />{result && <p className="mb-4 rounded-md bg-teal-50 p-4 font-semibold text-teal-700">{result}</p>}<div className="grid gap-4">{exam.questions.map((item, index) => <article className="rounded-md bg-white p-6 shadow-sm" key={item.questionId}><h2 className="font-semibold">{index + 1}. {item.question.questionContent}</h2><div className="mt-4 grid gap-2">{(item.question.questionType === 'TRUE_FALSE' ? [{ optionLabel: 'true', optionContent: '正确', id: 1 }, { optionLabel: 'false', optionContent: '错误', id: 2 }] : item.question.options).map((option) => <label className="flex items-center gap-3 rounded-md border p-3 text-sm" key={option.id}><input type="radio" name={`question-${item.questionId}`} onChange={() => setAnswers({ ...answers, [item.questionId]: option.optionLabel })} />{item.question.questionType === 'TRUE_FALSE' ? option.optionContent : `${option.optionLabel}. ${option.optionContent}`}</label>)}</div></article>)}<button className="justify-self-end rounded-md bg-teal-600 px-6 py-3 font-semibold text-white" onClick={submit}>提交考试</button></div></>;
}

function FilterBar({ value, onChange }: { value: ListFilter; onChange: (value: ListFilter) => void }) {
  return <div className="mb-5 flex items-center gap-6 rounded-md bg-white px-6 py-5 shadow-sm"><span className="text-sm text-slate-400">筛选</span>{([['all', '全部'], ['completed', '已完成'], ['pending', '未完成']] as const).map(([id, label]) => <label className="flex cursor-pointer items-center gap-2 text-sm" key={id}><input type="radio" checked={value === id} onChange={() => onChange(id)} />{label}</label>)}</div>;
}

function LearningRecords({ user, token, resource, score, summary }: {
  user: CurrentUser; token: string; resource: TrainingResource; score?: CourseScore; summary: ScoreSummary;
}) {
  const [learning, setLearning] = useState<CourseLearningStatus | null>(null);
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [discussions, setDiscussions] = useState<Discussion[]>([]);
  const profile = useMemo(() => {
    const resolved = resolveUserProfile(user);
    return { displayName: resolved.displayName, avatarUrl: resolved.avatarDataUrl };
  }, [user]);

  useEffect(() => {
    api<CourseLearningStatus>(`/training/learning/resources/${resource.id}`, {}, token).then(setLearning).catch(() => setLearning(null));
    api<Assignment[]>(`/training/resources/${resource.id}/assignments`, {}, token).then(setAssignments).catch(() => setAssignments([]));
    api<Discussion[]>(`/training/resources/${resource.id}/discussions`, {}, token).then(setDiscussions).catch(() => setDiscussions([]));
  }, [resource.id, token]);

  const progress = learning?.requiredSeconds
    ? Math.min(100, Math.round((learning.learnedSeconds / learning.requiredSeconds) * 100))
    : 0;
  const learnedMinutes = Math.floor((learning?.learnedSeconds || 0) / 60);
  const completedAssignments = assignments.filter((item) => Boolean(item.submissionStatus)).length;
  const replyCount = discussions.reduce((total, item) => total + item.replyCount, 0);
  const completedChapters = Math.min(3, Math.floor((progress / 100) * 3));
  const tasks = [
    { label: '章节学习', current: completedChapters, total: 3, percent: progress },
    { label: '课程作业', current: completedAssignments, total: assignments.length, percent: assignments.length ? Math.round((completedAssignments / assignments.length) * 100) : 0 },
    { label: '在线考试', current: score?.passed ? 1 : 0, total: score?.examId ? 1 : 0, percent: score?.passed ? 100 : 0 },
  ];

  return <div className="space-y-5">
    <article className="rounded-md bg-white px-7 py-8 shadow-sm sm:px-10">
      <div className="flex items-center gap-5">
        <img className="h-20 w-20 rounded-full object-cover ring-4 ring-teal-50" src={profile.avatarUrl} alt={`${profile.displayName}的头像`} />
        <div><h1 className="text-xl font-semibold">{profile.displayName}</h1><p className="mt-2 text-sm text-slate-500">当前课程：{resource.title}</p></div>
      </div>
    </article>

    <section className="grid gap-5 lg:grid-cols-12">
      <article className="rounded-md bg-white p-7 shadow-sm lg:col-span-6">
        <h2 className="text-lg font-semibold">课程学习进度</h2>
        <div className="mt-7 flex flex-col gap-7 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex gap-10"><div><strong className="text-3xl">{completedChapters}<small className="ml-1 text-base font-normal">/3</small></strong><span className="mt-2 block text-sm text-slate-500">完成章节</span></div><div><strong className="text-3xl">{learning?.completed ? '已完成' : '学习中'}</strong><span className="mt-2 block text-sm text-slate-500">当前状态</span></div></div>
          <div className="grid h-28 w-28 shrink-0 place-items-center rounded-full" style={{ background: `conic-gradient(#5ed19a ${progress}%, #e8edf2 0)` }}><div className="grid h-20 w-20 place-items-center rounded-full bg-white text-center"><span className="text-xs text-slate-400">完成率<strong className="mt-1 block text-2xl text-slate-900">{progress}%</strong></span></div></div>
        </div>
      </article>

      <article className="rounded-md bg-white p-7 shadow-sm lg:col-span-3"><h2 className="text-lg font-semibold">在线学习</h2><strong className="mt-8 block text-4xl font-medium">{learnedMinutes}<small className="ml-1 text-base font-normal">分钟</small></strong><span className="mt-3 block text-sm text-slate-500">本课程累计学习时长</span></article>
      <article className="rounded-md bg-white p-7 shadow-sm lg:col-span-3"><h2 className="text-lg font-semibold">考试成绩</h2><strong className={`mt-8 block text-4xl font-medium ${score?.passed ? 'text-emerald-600' : 'text-slate-900'}`}>{score?.bestScore || 0}<small className="ml-1 text-base font-normal">分</small></strong><span className="mt-3 block text-sm text-slate-500">{score?.examId ? (score.passed ? '本课程考试已通过' : '及格分为60分') : '本课程考试暂未发布'}</span></article>

      <article className="rounded-md bg-white p-7 shadow-sm lg:col-span-5"><h2 className="text-lg font-semibold">学习互动</h2><div className="mt-8 grid grid-cols-3 gap-4"><RecordMetric value={discussions.length} label="发帖" /><RecordMetric value={replyCount} label="回复" /><RecordMetric value={completedAssignments} label="已交作业" /></div><div className="mt-8 border-t border-slate-100 pt-5 text-sm text-slate-500">整体平均成绩 <strong className="ml-2 text-base text-slate-900">{summary?.averageScore || 0}分</strong></div></article>
      <article className="rounded-md bg-white p-7 shadow-sm lg:col-span-7"><h2 className="text-lg font-semibold">学习任务</h2><div className="mt-7 grid gap-7">{tasks.map((task) => <div className="grid grid-cols-[88px_1fr_auto] items-center gap-4" key={task.label}><span className="text-sm font-medium">{task.label}</span><div className="h-2 overflow-hidden rounded-full bg-slate-100"><span className="block h-full rounded-full bg-teal-500" style={{ width: `${task.percent}%` }} /></div><strong className="min-w-10 text-right text-sm">{task.current}/{task.total}</strong></div>)}</div></article>
    </section>
  </div>;
}

function RecordMetric({ label, value }: { label: string; value: number }) { return <div><strong className="text-3xl font-medium">{value}</strong><span className="mt-2 block text-sm text-slate-500">{label}</span></div>; }
function MistakeBook({ resourceId, token }: { resourceId: number; token: string }) {
  const [items, setItems] = useState<MistakeQuestion[]>([]);
  const [error, setError] = useState('');
  useEffect(() => { api<MistakeQuestion[]>(`/training/learning/resources/${resourceId}/mistakes`, {}, token).then(setItems).catch((e) => setError(e.message)); }, [resourceId, token]);
  return <><PageTitle eyebrow="Mistake review" title="错题集" description="汇总最近一次考试中的错题，结合标准答案和解析集中复习。" />{error && <p className="mb-4 rounded-md bg-red-50 p-4 text-sm text-red-700">{error}</p>}<div className="grid gap-4">{items.map((item, index) => <article className="rounded-md bg-white p-6 shadow-sm" key={item.answerId}><div className="flex items-center justify-between gap-4"><strong>错题 {index + 1}</strong><span className="rounded bg-red-50 px-2.5 py-1 text-xs font-semibold text-red-600">{item.questionType === 'TRUE_FALSE' ? '判断题' : '选择题'}</span></div><h2 className="mt-5 text-lg font-semibold">{item.questionContent}</h2><div className="mt-5 grid gap-3 sm:grid-cols-2"><p className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-700"><span className="block text-xs text-red-500">你的答案</span>{formatExamAnswer(item.userAnswer)}</p><p className="rounded-md border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-700"><span className="block text-xs text-emerald-600">正确答案</span>{formatExamAnswer(item.standardAnswer)}</p></div><div className="mt-4 border-l-4 border-amber-400 bg-amber-50 p-4"><strong className="text-sm">知识点解析</strong><p className="mt-2 text-sm leading-6 text-slate-600">{item.analysis || '请回到课程章节复习对应护理操作规范，并核对关键步骤。'}</p></div></article>)}{!error && items.length === 0 && <div className="rounded-md bg-white py-20 text-center shadow-sm"><ClipboardCheck className="mx-auto text-emerald-500" size={40} /><h2 className="mt-4 font-semibold">本课程暂无错题</h2><p className="mt-2 text-sm text-slate-400">完成考试后，系统会自动整理最近一次答错的题目。</p></div>}</div></>;
}

function formatExamAnswer(value: string) { if (value === 'true') return '正确'; if (value === 'false') return '错误'; return value; }
