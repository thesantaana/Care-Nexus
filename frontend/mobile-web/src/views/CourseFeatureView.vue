<template>
  <section class="page course-feature-page">
    <RouterLink class="back-link" to="/training"><AppIcon name="arrow-left" />全部课程</RouterLink>
    <header class="course-feature-header">
      <div><p class="eyebrow">COURSE WORKSPACE</p><h1>{{ resource?.title || '课程学习' }}</h1><p>{{ sectionMeta.description }}</p></div>
    </header>
    <CourseWorkspaceNav :resource-id="resourceId" :active="section" />
    <p v-if="error" class="feature-error">{{ error }}</p>
    <div v-if="loading" class="feature-empty">正在读取课程数据…</div>

    <template v-else-if="section === 'discussions'">
      <form class="composer" @submit.prevent="publishDiscussion"><input v-model="draft.title" placeholder="讨论标题" maxlength="160" required><textarea v-model="draft.content" placeholder="分享问题、经验或学习体会" maxlength="3000" required /><button class="primary-button">发布讨论</button></form>
      <article v-for="topic in discussions" :key="topic.id" class="feature-card discussion-card">
        <div class="discussion-author"><span>{{ topic.authorName?.slice(0,1) }}</span><strong>{{ topic.authorName }}</strong><time>{{ formatTime(topic.createdAt) }}</time></div>
        <h2>{{ topic.title }}</h2><p>{{ topic.content }}</p>
        <div class="discussion-actions"><button @click="likeTopic(topic)">♡ {{ topic.likeCount || '点赞' }}</button><button @click="openReplies(topic)">评论 {{ topic.replyCount || 0 }}</button></div>
        <section v-if="expandedId === topic.id" class="replies"><p v-for="reply in replies" :key="reply.id"><strong>{{ reply.authorName }}：</strong>{{ reply.content }}</p><form @submit.prevent="sendReply(topic)"><input v-model="replyText" placeholder="写下评论" required><button>发送</button></form></section>
      </article>
      <div v-if="!discussions.length" class="feature-empty">还没有讨论，发布第一条学习动态吧。</div>
    </template>

    <template v-else-if="section === 'assignments'">
      <article v-for="item in assignments" :key="item.id" class="feature-card assignment-card">
        <div><span class="type-chip">作业</span><h2>{{ item.title }}</h2><p>{{ item.content }}</p></div>
        <div v-if="item.type === 'TRUE_FALSE'" class="answer-options"><label><input v-model="assignmentAnswers[item.id]" type="radio" value="true">正确</label><label><input v-model="assignmentAnswers[item.id]" type="radio" value="false">错误</label></div>
        <div v-else-if="assignmentOptions(item).length" class="answer-options"><label v-for="option in assignmentOptions(item)" :key="option"><input v-model="assignmentAnswers[item.id]" type="radio" :value="option">{{ option }}</label></div>
        <textarea v-else v-model="assignmentAnswers[item.id]" placeholder="填写作业内容" />
        <button class="primary-button" :disabled="!assignmentAnswers[item.id]" @click="submitOneAssignment(item)">{{ item.submissionStatus ? '重新提交' : '提交作业' }}</button>
        <strong v-if="item.score != null" :class="Number(item.score) >= 60 ? 'passed' : 'failed'">{{ item.score }} 分</strong>
      </article>
      <div v-if="!assignments.length" class="feature-empty">本课程暂未发布作业。</div>
    </template>

    <template v-else-if="section === 'exam'">
      <article v-if="exam" class="feature-card exam-card"><h2>{{ exam.examName }}</h2><p>及格分 {{ exam.passScore }} 分，最多作答 {{ exam.maxAttempts }} 次。</p><div v-for="(entry,index) in exam.questions" :key="entry.questionId" class="exam-question"><strong>{{ index+1 }}. {{ entry.question.questionContent }}</strong><label v-for="option in examOptions(entry.question)" :key="option.value"><input v-model="examAnswers[entry.questionId]" type="radio" :value="option.value">{{ option.label }}</label></div><button class="primary-button" :disabled="!canSubmitExam" @click="submitExam">提交考试</button><p v-if="examResult" class="result-banner">{{ examResult }}</p></article>
      <div v-else class="feature-empty">本课程考试暂未发布，完成学习后再来查看。</div>
    </template>

    <template v-else-if="section === 'mistakes'">
      <article v-for="(item,index) in mistakes" :key="item.answerId" class="feature-card mistake-card"><span class="type-chip danger">错题 {{ index+1 }}</span><h2>{{ item.questionContent }}</h2><p class="wrong">你的答案：{{ formatAnswer(item.userAnswer) }}</p><p class="correct">正确答案：{{ formatAnswer(item.standardAnswer) }}</p><div class="analysis"><strong>知识点解析</strong><p>{{ item.analysis || '请回到课程章节复习对应护理规范，并核对关键操作步骤。' }}</p></div></article>
      <div v-if="!mistakes.length" class="feature-empty">当前课程暂无错题，继续保持。</div>
    </template>

    <template v-else-if="section === 'records'">
      <article class="record-profile"><img :src="sessionState.user?.avatarUrl || '/assets/default-avatar.png'"><div><h2>{{ sessionState.user?.displayName }}</h2><p>{{ resource?.title }}</p></div></article>
      <div class="record-grid"><article><span>课程进度</span><strong>{{ progressPercent }}%</strong></article><article><span>学习时长</span><strong>{{ Math.floor((learning?.learnedSeconds || 0)/60) }} 分钟</strong></article><article><span>考试成绩</span><strong>{{ courseScore?.bestScore || 0 }} 分</strong></article><article><span>培训状态</span><strong>{{ learning?.completed ? '已完成' : '学习中' }}</strong></article></div>
    </template>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import AppIcon from '../components/AppIcon.vue'
import CourseWorkspaceNav from '../components/CourseWorkspaceNav.vue'
import { sessionState } from '../session.js'
import { createCourseDiscussion, createDiscussionReply, getCourseAssignments, getCourseDiscussions, getCourseLearningStatus, getCourseMistakes, getDiscussionReplies, getTrainingExam, getTrainingResource, getTrainingScores, submitAssignment, submitTrainingExam, toggleDiscussionLike } from '../api/training.js'

const props = defineProps({ resourceId: { type: String, required: true }, section: { type: String, required: true } })
const resource=ref(null), loading=ref(false), error=ref(''), discussions=ref([]), assignments=ref([]), mistakes=ref([]), exam=ref(null), learning=ref(null), scores=ref(null)
const draft=ref({title:'',content:''}), expandedId=ref(null), replies=ref([]), replyText=ref(''), assignmentAnswers=ref({}), examAnswers=ref({}), examResult=ref('')
const meta={discussions:['课程讨论','与其他学习者分享问题与经验。'],assignments:['课程作业','完成老师发布的课程作业并查看成绩。'],exam:['课程考试','完成课程学习后参加知识考核。'],mistakes:['错题集','集中复习最近一次考试中的错题。'],records:['学习记录','查看当前课程的学习进度与成绩。']}
const sectionMeta=computed(()=>({title:meta[props.section]?.[0],description:meta[props.section]?.[1]}))
const courseScore=computed(()=>scores.value?.courseScores?.find(item=>Number(item.resourceId)===Number(props.resourceId)))
const progressPercent=computed(()=>learning.value?.requiredSeconds?Math.min(100,Math.round(learning.value.learnedSeconds/learning.value.requiredSeconds*100)):0)
const canSubmitExam=computed(()=>exam.value?.questions?.every(item=>examAnswers.value[item.questionId]))

async function load(){loading.value=true;error.value='';try{resource.value=await getTrainingResource(props.resourceId);learning.value=await getCourseLearningStatus(props.resourceId).catch(()=>null);scores.value=await getTrainingScores().catch(()=>null);if(props.section==='discussions')discussions.value=await getCourseDiscussions(props.resourceId);if(props.section==='assignments')assignments.value=await getCourseAssignments(props.resourceId);if(props.section==='mistakes')mistakes.value=await getCourseMistakes(props.resourceId);if(props.section==='exam'){const examId=courseScore.value?.examId;exam.value=examId?await getTrainingExam(examId):null}}catch(e){error.value=e.message||'课程数据加载失败'}finally{loading.value=false}}
async function publishDiscussion(){await createCourseDiscussion(props.resourceId,draft.value.title,draft.value.content);draft.value={title:'',content:''};discussions.value=await getCourseDiscussions(props.resourceId)}
async function openReplies(topic){expandedId.value=expandedId.value===topic.id?null:topic.id;if(expandedId.value)replies.value=await getDiscussionReplies(topic.id)}
async function sendReply(topic){await createDiscussionReply(topic.id,replyText.value);replyText.value='';replies.value=await getDiscussionReplies(topic.id);discussions.value=await getCourseDiscussions(props.resourceId)}
async function likeTopic(topic){await toggleDiscussionLike(topic.id);discussions.value=await getCourseDiscussions(props.resourceId)}
function assignmentOptions(item){try{return JSON.parse(item.optionsJson||'[]')}catch{return[]}}
async function submitOneAssignment(item){const result=await submitAssignment(item.id,assignmentAnswers.value[item.id]);Object.assign(item,result)}
function examOptions(question){return question.questionType==='TRUE_FALSE'?[{value:'true',label:'正确'},{value:'false',label:'错误'}]:(question.options||[]).map(item=>({value:item.optionLabel,label:`${item.optionLabel}. ${item.optionContent}`}))}
async function submitExam(){const result=await submitTrainingExam(exam.value.id,exam.value.questions.map(item=>({questionId:item.questionId,answer:examAnswers.value[item.questionId]})));examResult.value=`本次成绩 ${result.score} 分，${result.passStatus==='PASSED'?'考试通过':'未达到及格分'}`;scores.value=await getTrainingScores()}
function formatAnswer(value){return value==='true'?'正确':value==='false'?'错误':value}
function formatTime(value){return value?new Date(value).toLocaleString('zh-CN'):''}
watch(()=>[props.resourceId,props.section],load)
onMounted(load)
</script>

<style scoped>
.course-feature-page{max-width:1180px}.course-feature-header{margin:16px 0 20px}.course-feature-header h1{margin:6px 0;font-size:clamp(26px,5vw,42px)}.feature-card,.composer,.record-profile,.record-grid article,.feature-empty{margin-bottom:14px;padding:20px;border:1px solid #dce6e8;border-radius:8px;background:#fff}.composer{display:grid;gap:10px}.composer input,.composer textarea,.replies input,.assignment-card textarea{width:100%;box-sizing:border-box;border:1px solid #ccdadd;border-radius:6px;padding:12px}.composer textarea,.assignment-card textarea{min-height:100px}.discussion-author{display:flex;align-items:center;gap:10px}.discussion-author>span{display:grid;width:38px;height:38px;place-items:center;border-radius:50%;background:#c9f7ed;color:#087e75;font-weight:800}.discussion-author time{margin-left:auto;color:#8ba0a7;font-size:12px}.discussion-card p{line-height:1.7}.discussion-actions{display:flex;gap:18px;border-top:1px solid #edf2f3;padding-top:12px}.discussion-actions button{border:0;background:none;color:#61747c}.replies{margin-top:14px;padding:14px;background:#f5f8f9}.replies form{display:flex;gap:8px}.replies button{border:0;border-radius:5px;background:#087e75;color:#fff;padding:0 14px}.assignment-card{display:grid;gap:14px}.type-chip{width:max-content;border-radius:4px;background:#e8f5f3;color:#087e75;padding:5px 9px;font-size:12px;font-weight:700}.type-chip.danger{background:#fff0ee;color:#c03b30}.answer-options{display:grid;gap:8px}.answer-options label,.exam-question label{display:flex;gap:9px;border:1px solid #dce6e8;border-radius:6px;padding:12px}.passed,.correct{color:#13835e}.failed,.wrong,.feature-error{color:#c03b30}.exam-question{display:grid;gap:10px;margin:20px 0;padding-top:18px;border-top:1px solid #edf2f3}.result-banner,.analysis{margin-top:15px;padding:14px;border-left:4px solid #17a984;background:#ecfaf6}.record-profile{display:flex;align-items:center;gap:16px}.record-profile img{width:70px;height:70px;border-radius:50%;object-fit:cover}.record-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:12px}.record-grid article{display:grid;gap:12px}.record-grid span{color:#667a82}.record-grid strong{font-size:24px}.feature-empty{text-align:center;color:#789099;padding-block:48px}@media(max-width:760px){.record-grid{grid-template-columns:1fr 1fr}.feature-card,.composer{padding:16px}.course-feature-header h1{font-size:28px}}
</style>
