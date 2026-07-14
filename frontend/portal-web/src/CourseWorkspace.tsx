import { useEffect, useMemo, useState, type FormEvent } from 'react';
import {
  ArrowLeft, BookOpen, CheckCircle2, ClipboardCheck, FileText, GraduationCap,
  ListChecks, LogOut, MessageCircle, Reply, Send, XCircle,
} from 'lucide-react';
import { api, type CurrentUser, type TrainingResource } from './portal';

type CourseSection = 'chapters' | 'discussions' | 'assignments' | 'exam' | 'mistakes' | 'records';
type CourseScore = { resourceId: number; examId: number | null; passScore: number; bestScore: number; passed: boolean };
type ScoreSummary = { averageScore: number; trainingPassed: boolean; courseScores: CourseScore[] } | null;
type Discussion = { id: number; title: string; content: string; authorName: string; replyCount: number; createdAt: string };
type DiscussionReply = { id: number; content: string; authorName: string; createdAt: string };
type Assignment = { id: number; title: string; type: string; content: string; optionsJson?: string; submissionStatus?: string; submittedAnswer?: string; score?: number };
type ExamOption = { id: number; optionLabel: string; optionContent: string };
type ExamQuestion = { questionId: number; score: number; question: { questionType: string; questionContent: string; options: ExamOption[] } };
type Exam = { id: number; examName: string; passScore: number; maxAttempts: number; questions: ExamQuestion[] };
type CourseLearningStatus = { resourceId: number; learnedSeconds: number; requiredSeconds: number; completed: boolean };
type ListFilter = 'all' | 'completed' | 'pending';

export function CourseWorkspace({ user, token, resource, scoreSummary, onBack, onLogout }: {
  user: CurrentUser; token: string; resource: TrainingResource; scoreSummary: ScoreSummary;
  onBack: () => void; onLogout: () => void;
}) {
  const [section, setSection] = useState<CourseSection>('chapters');
  const score = scoreSummary?.courseScores.find((item) => item.resourceId === resource.id);
  const navigation = [
    { id: 'chapters' as const, label: '章节', icon: ListChecks },
    { id: 'discussions' as const, label: '讨论', icon: MessageCircle },
    { id: 'assignments' as const, label: '作业', icon: FileText },
    { id: 'exam' as const, label: '考试', icon: GraduationCap },
    { id: 'mistakes' as const, label: '错题集', icon: XCircle },
    { id: 'records' as const, label: '学习记录', icon: BookOpen },
  ];

  return <main className="min-h-screen bg-[#f1f4f8] pl-[78px] text-slate-900 md:pl-[220px]">
    <header className="fixed inset-x-0 top-0 z-30 flex h-16 items-center justify-between border-b border-slate-200 bg-white px-5 md:pl-[244px]">
      <button className="inline-flex items-center gap-2 text-sm font-medium text-slate-600 hover:text-teal-700" onClick={onBack}><ArrowLeft size={18} />全部课程</button>
      <div className="flex items-center gap-3"><span className="grid h-9 w-9 place-items-center rounded-full bg-teal-600 font-semibold text-white">{user.displayName.slice(0, 1)}</span><strong className="hidden text-sm sm:block">{user.displayName}</strong><button className="rounded-md p-2 text-slate-500 hover:bg-slate-100" onClick={onLogout} title="退出登录"><LogOut size={18} /></button></div>
    </header>
    <aside className="fixed inset-y-0 left-0 z-40 w-[78px] border-r border-slate-200 bg-white pt-4 md:w-[220px]">
      <div className="px-3 md:px-5"><div className="flex items-center gap-3"><span className="grid h-10 w-10 shrink-0 place-items-center rounded-md bg-teal-700 text-white"><CheckCircle2 size={20} /></span><strong className="hidden md:block">CareNexus</strong></div><div className="mt-6 hidden md:block"><img className="h-24 w-full rounded-md object-cover" src={resource.coverUrl || '/assets/default-course-cover.png'} alt="" /><h2 className="mt-3 line-clamp-2 text-sm font-semibold">{resource.title}</h2></div></div>
      <nav className="mt-7 grid" aria-label="课程导航">{navigation.map((item) => <button key={item.id} className={`flex min-h-14 items-center justify-center gap-3 border-l-4 px-3 text-sm transition md:justify-start md:px-6 ${section === item.id ? 'border-teal-600 bg-teal-50 font-semibold text-teal-700' : 'border-transparent text-slate-500 hover:bg-slate-50'}`} onClick={() => setSection(item.id)}><item.icon size={19} /><span className="hidden md:inline">{item.label}</span></button>)}</nav>
    </aside>
    <section className="min-h-screen px-5 pb-12 pt-24 sm:px-8 lg:px-12">
      <div className="mx-auto max-w-6xl">
        {section === 'chapters' && <Chapters resource={resource} score={score} token={token} />}
        {section === 'discussions' && <Discussions resourceId={resource.id} token={token} />}
        {section === 'assignments' && <Assignments resourceId={resource.id} token={token} />}
        {section === 'exam' && <ExamPanel resourceId={resource.id} score={score} token={token} onUpdated={() => window.location.reload()} />}
        {section === 'mistakes' && <EmptyPanel title="暂无错题" description="提交考试后，答错的题目会归入这里，便于集中复习。" />}
        {section === 'records' && <LearningRecords resource={resource} score={score} summary={scoreSummary} />}
      </div>
    </section>
  </main>;
}

function PageTitle({ eyebrow, title, description }: { eyebrow: string; title: string; description: string }) {
  return <header className="mb-7"><p className="text-xs font-bold uppercase tracking-[0.18em] text-teal-700">{eyebrow}</p><h1 className="mt-2 text-3xl font-semibold">{title}</h1><p className="mt-2 text-sm text-slate-500">{description}</p></header>;
}

function Chapters({ resource, score, token }: { resource: TrainingResource; score?: CourseScore; token: string }) {
  const [detail, setDetail] = useState<TrainingResource>(resource);
  useEffect(() => { api<TrainingResource>(`/training/resources/${resource.id}`, {}, token).then(setDetail).catch(() => undefined); }, [resource.id, token]);
  const chapters = [
    { title: '课程导学与学习目标', detail: detail.summary || '了解本课程的照护目标、风险与规范要求。' },
    { title: `${detail.categoryName || '护理'}核心知识`, detail: '阅读课程资料，掌握关键操作步骤和注意事项。' },
    { title: '课程复习与考核准备', detail: '回顾重点内容，完成课后作业并参加课程考试。' },
  ];
  return <><PageTitle eyebrow="Course chapters" title="课程章节" description="按照课程资料组织学习内容，完成后进入作业与考试。" /><div className="rounded-md bg-white shadow-sm"><div className="flex items-center justify-between border-b border-slate-100 p-6"><div><strong>学习目录</strong><span className="ml-3 text-sm text-slate-400">{chapters.length} 个章节</span></div><span className={`text-sm font-semibold ${score?.passed ? 'text-emerald-600' : 'text-slate-500'}`}>{score?.passed ? '课程已通过' : '学习中'}</span></div>{chapters.map((chapter, index) => <article className="flex gap-4 border-b border-slate-100 p-6 last:border-0" key={chapter.title}><span className="grid h-8 w-8 shrink-0 place-items-center rounded-full bg-teal-50 text-sm font-semibold text-teal-700">{index + 1}</span><div><h2 className="font-semibold">{chapter.title}</h2><p className="mt-2 text-sm leading-6 text-slate-500">{chapter.detail}</p></div></article>)}</div></>;
}

function Discussions({ resourceId, token }: { resourceId: number; token: string }) {
  const [topics, setTopics] = useState<Discussion[]>([]); const [active, setActive] = useState<Discussion | null>(null);
  const [replies, setReplies] = useState<DiscussionReply[]>([]); const [showForm, setShowForm] = useState(false);
  const [title, setTitle] = useState(''); const [content, setContent] = useState(''); const [reply, setReply] = useState(''); const [error, setError] = useState('');
  const load = () => api<Discussion[]>(`/training/resources/${resourceId}/discussions`, {}, token).then(setTopics).catch((e) => setError(e.message));
  useEffect(() => { void load(); }, [resourceId, token]);
  async function createTopic(event: FormEvent) { event.preventDefault(); await api(`/training/resources/${resourceId}/discussions`, { method: 'POST', body: JSON.stringify({ title, content }) }, token); setTitle(''); setContent(''); setShowForm(false); load(); }
  async function openTopic(topic: Discussion) { setActive(topic); setReplies(await api<DiscussionReply[]>(`/training/discussions/${topic.id}/replies`, {}, token)); }
  async function sendReply(event: FormEvent) { event.preventDefault(); if (!active) return; await api(`/training/discussions/${active.id}/replies`, { method: 'POST', body: JSON.stringify({ content: reply }) }, token); setReply(''); await openTopic(active); load(); }
  return <><PageTitle eyebrow="Course discussion" title="课程讨论" description="围绕课程内容发起话题，与其他学习者交流。" /><div className="grid gap-5 lg:grid-cols-[1fr_320px]"><section className="rounded-md bg-white p-6 shadow-sm"><div className="mb-5 flex items-center justify-between"><strong>全部话题</strong><button className="rounded-md bg-teal-600 px-4 py-2 text-sm font-semibold text-white" onClick={() => setShowForm(!showForm)}>+ 新建话题</button></div>{showForm && <form className="mb-6 grid gap-3 rounded-md bg-slate-50 p-4" onSubmit={createTopic}><input className="rounded-md border p-3 text-sm" value={title} onChange={(e) => setTitle(e.target.value)} placeholder="话题标题" required /><textarea className="min-h-24 rounded-md border p-3 text-sm" value={content} onChange={(e) => setContent(e.target.value)} placeholder="分享你的问题或看法" required /><button className="justify-self-end rounded-md bg-teal-600 px-4 py-2 text-sm text-white">发布</button></form>}{error && <p className="text-sm text-red-600">{error}</p>}{topics.map((topic) => <button className="block w-full border-b border-slate-100 py-5 text-left last:border-0" key={topic.id} onClick={() => openTopic(topic)}><div className="flex justify-between gap-4"><strong>{topic.title}</strong><span className="text-xs text-slate-400">{topic.replyCount} 回复</span></div><p className="mt-2 line-clamp-2 text-sm text-slate-500">{topic.content}</p><small className="mt-3 block text-slate-400">{topic.authorName} · {new Date(topic.createdAt).toLocaleString('zh-CN')}</small></button>)}{topics.length === 0 && <p className="py-16 text-center text-sm text-slate-400">暂无讨论，欢迎发布第一个话题</p>}</section><aside className="rounded-md bg-white p-5 shadow-sm"><h2 className="font-semibold">{active?.title || '话题回复'}</h2>{active ? <><p className="mt-3 text-sm leading-6 text-slate-600">{active.content}</p><div className="mt-5 grid gap-3">{replies.map((item) => <div className="rounded-md bg-slate-50 p-3" key={item.id}><p className="text-sm">{item.content}</p><small className="mt-2 block text-slate-400">{item.authorName}</small></div>)}</div><form className="mt-5 flex gap-2" onSubmit={sendReply}><input className="min-w-0 flex-1 rounded-md border px-3 text-sm" value={reply} onChange={(e) => setReply(e.target.value)} placeholder="写下回复" required /><button className="grid h-10 w-10 place-items-center rounded-md bg-teal-600 text-white"><Send size={17} /></button></form></> : <p className="mt-4 text-sm text-slate-400">选择左侧话题查看并回复</p>}</aside></div></>;
}

function Assignments({ resourceId, token }: { resourceId: number; token: string }) {
  const [items, setItems] = useState<Assignment[]>([]); const [answers, setAnswers] = useState<Record<number, string>>({}); const [message, setMessage] = useState('');
  const [filter, setFilter] = useState<ListFilter>('all'); const [selected, setSelected] = useState<Assignment | null>(null);
  const load = () => api<Assignment[]>(`/training/resources/${resourceId}/assignments`, {}, token).then(setItems);
  useEffect(() => { void load(); }, [resourceId, token]);
  async function submit(item: Assignment) { const answer = answers[item.id] ?? item.submittedAnswer ?? ''; if (!answer.trim()) return; const updated = await api<Assignment>(`/training/assignments/${item.id}/submission`, { method: 'POST', body: JSON.stringify({ answer }) }, token); setMessage('作业已提交，可随时重新作答'); setSelected(updated); await load(); }
  const visible = items.filter((item) => filter === 'all' || (filter === 'completed' ? Boolean(item.submissionStatus) : !item.submissionStatus));
  if (!selected) return <><PageTitle eyebrow="Course assignments" title="课程作业" description="选择一项作业后进入作答，已完成作业可以无限重做。" /><FilterBar value={filter} onChange={setFilter} /> <div className="rounded-md bg-white shadow-sm">{visible.map((item) => <button className="flex w-full items-center gap-5 border-b border-slate-100 px-6 py-6 text-left last:border-0 hover:bg-slate-50" key={item.id} onClick={() => setSelected(item)}><span className="grid h-14 w-14 shrink-0 place-items-center rounded-md bg-slate-200 text-sm font-semibold text-slate-600">作业</span><span className="min-w-0 flex-1"><strong className="block truncate">{item.title}</strong><small className={`mt-2 block ${item.submissionStatus ? 'text-emerald-600' : 'text-slate-400'}`}>{item.submissionStatus ? `已完成${item.score != null ? ` · ${item.score}分` : ''}` : '未完成'}</small></span><span className="text-sm font-semibold text-teal-700">进入作答 →</span></button>)}{visible.length === 0 && <p className="py-20 text-center text-sm text-slate-400">当前筛选下暂无作业</p>}</div></>;
  let options: string[] = []; try { options = JSON.parse(selected.optionsJson || '[]'); } catch { options = []; }
  return <><button className="mb-5 inline-flex items-center gap-2 text-sm font-semibold text-slate-600" onClick={() => { setSelected(null); setMessage(''); }}><ArrowLeft size={17} />返回作业列表</button><PageTitle eyebrow="Assignment" title={selected.title} description={selected.submissionStatus ? '你已完成本作业，可以修改答案并重新提交。' : '完成作答后提交，客观题将自动评分。'} />{message && <p className="mb-4 rounded-md bg-emerald-50 p-3 text-sm text-emerald-700">{message}</p>}<article className="rounded-md bg-white p-7 shadow-sm"><p className="leading-7 text-slate-700">{selected.content}</p><div className="mt-6">{selected.type === 'TEXT' ? <textarea className="min-h-40 w-full rounded-md border p-4 text-sm" value={answers[selected.id] ?? selected.submittedAnswer ?? ''} onChange={(e) => setAnswers({ ...answers, [selected.id]: e.target.value })} placeholder="输入作业内容" /> : <div className="grid gap-3">{options.map((option, index) => { const value = selected.type === 'TRUE_FALSE' ? (index === 0 ? 'true' : 'false') : option; return <label className="flex items-center gap-3 rounded-md border p-4 text-sm" key={option}><input type="radio" name={`assignment-${selected.id}`} checked={(answers[selected.id] ?? selected.submittedAnswer) === value} onChange={() => setAnswers({ ...answers, [selected.id]: value })} />{option}</label>; })}</div>}<button className="mt-6 rounded-md bg-teal-600 px-6 py-3 font-semibold text-white" onClick={() => submit(selected)}>{selected.submissionStatus ? '重新提交' : '提交作业'}</button></div></article></>;
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

function LearningRecords({ resource, score, summary }: { resource: TrainingResource; score?: CourseScore; summary: ScoreSummary }) {
  return <><PageTitle eyebrow="Learning record" title="学习记录" description="查看本课程成绩及整体培训完成情况。" /><section className="grid gap-5 md:grid-cols-3"><article className="rounded-md bg-white p-6 shadow-sm md:col-span-3"><div className="flex items-center gap-4"><img className="h-16 w-16 rounded-md object-cover" src={resource.coverUrl || '/assets/default-course-cover.png'} alt="" /><div><h2 className="font-semibold">{resource.title}</h2><p className="mt-1 text-sm text-slate-500">{score?.passed ? '课程已通过' : '课程学习中'}</p></div></div></article><Metric label="本课程最高成绩" value={`${score?.bestScore || 0}分`} /><Metric label="整体平均成绩" value={`${summary?.averageScore || 0}分`} /><Metric label="培训状态" value={summary?.trainingPassed ? '已通过' : '进行中'} /></section></>;
}

function Metric({ label, value }: { label: string; value: string }) { return <article className="rounded-md bg-white p-6 shadow-sm"><strong className="text-3xl text-teal-700">{value}</strong><span className="mt-2 block text-sm text-slate-500">{label}</span></article>; }
function EmptyPanel({ title, description }: { title: string; description: string }) { return <div className="grid min-h-96 place-items-center rounded-md bg-white text-center shadow-sm"><div><ClipboardCheck className="mx-auto text-slate-300" size={40} /><h2 className="mt-4 font-semibold">{title}</h2><p className="mt-2 text-sm text-slate-400">{description}</p></div></div>; }
