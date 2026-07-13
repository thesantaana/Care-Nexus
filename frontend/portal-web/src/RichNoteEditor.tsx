import { useEffect, useRef, useState, type ReactNode } from 'react';
import { Color } from '@tiptap/extension-color';
import Highlight from '@tiptap/extension-highlight';
import Image from '@tiptap/extension-image';
import Link from '@tiptap/extension-link';
import Table from '@tiptap/extension-table';
import TableCell from '@tiptap/extension-table-cell';
import TableHeader from '@tiptap/extension-table-header';
import TableRow from '@tiptap/extension-table-row';
import TaskItem from '@tiptap/extension-task-item';
import TaskList from '@tiptap/extension-task-list';
import TextAlign from '@tiptap/extension-text-align';
import TextStyle from '@tiptap/extension-text-style';
import Underline from '@tiptap/extension-underline';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import {
  AlignCenter, AlignLeft, AlignRight, Bold, CheckSquare, Code2, Heading1, Heading2,
  Highlighter, ImagePlus, Italic, Link2, List, ListOrdered, Quote, Redo2, Save,
  Strikethrough, Table2, Underline as UnderlineIcon, Undo2, X,
} from 'lucide-react';

type Note = { title: string; content: string } | null;

async function noteApi<T>(path: string, token: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers);
  headers.set('Authorization', `Bearer ${token}`);
  if (options.body && !(options.body instanceof FormData)) headers.set('Content-Type', 'application/json');
  const response = await fetch(`/api/v1${path}`, { ...options, headers });
  const payload = await response.json();
  if (!response.ok || payload.code !== 'SUCCESS') throw new Error(payload.message || '笔记操作失败。');
  return payload.data as T;
}

function ToolButton({ title, active = false, disabled = false, onClick, children }: {
  title: string; active?: boolean; disabled?: boolean; onClick: () => void; children: ReactNode;
}) {
  return <button className={`note-tool ${active ? 'is-active' : ''}`} type="button" title={title} disabled={disabled} onClick={onClick}>{children}</button>;
}

export function RichNoteEditor({ resourceId, resourceTitle, token, onClose, onSaved }: {
  resourceId: number; resourceTitle: string; token: string; onClose: () => void; onSaved: () => void;
}) {
  const [title, setTitle] = useState(`${resourceTitle}学习笔记`);
  const [message, setMessage] = useState('正在读取笔记…');
  const [saving, setSaving] = useState(false);
  const imageInput = useRef<HTMLInputElement>(null);
  const editor = useEditor({
    extensions: [
      StarterKit, Underline, Link.configure({ openOnClick: false }), Image.configure({ allowBase64: false }),
      TextAlign.configure({ types: ['heading', 'paragraph'] }), Highlight.configure({ multicolor: true }),
      TextStyle, Color, TaskList, TaskItem.configure({ nested: true }),
      Table.configure({ resizable: true }), TableRow, TableHeader, TableCell,
    ],
    content: '<p></p>',
    editorProps: { attributes: { class: 'note-document-body' } },
  });

  useEffect(() => {
    noteApi<Note>(`/training/notes/resource/${resourceId}`, token)
      .then((note) => {
        if (note) {
          setTitle(note.title);
          editor?.commands.setContent(note.content || '<p></p>');
        }
        setMessage('');
      })
      .catch((error) => setMessage(error instanceof Error ? error.message : '笔记读取失败。'));
  }, [editor, resourceId, token]);

  async function save() {
    if (!editor || saving) return;
    if (!title.trim()) { setMessage('请输入笔记标题。'); return; }
    setSaving(true);
    setMessage('');
    try {
      await noteApi(`/training/notes/resource/${resourceId}`, token, {
        method: 'PUT', body: JSON.stringify({ title: title.trim(), content: editor.getHTML() }),
      });
      setMessage('笔记已保存到账号。');
      onSaved();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : '笔记保存失败。');
    } finally {
      setSaving(false);
    }
  }

  function setLink() {
    const previous = editor?.getAttributes('link').href || '';
    const url = window.prompt('请输入链接地址', previous);
    if (url === null || !editor) return;
    if (!url.trim()) editor.chain().focus().unsetLink().run();
    else editor.chain().focus().extendMarkRange('link').setLink({ href: url.trim() }).run();
  }

  async function uploadImage(file?: File) {
    if (!file || !editor) return;
    setMessage('正在上传图片…');
    const form = new FormData();
    form.append('file', file);
    try {
      const result = await noteApi<{ url: string }>('/training/notes/images', token, { method: 'POST', body: form });
      editor.chain().focus().setImage({ src: result.url, alt: file.name }).run();
      setMessage('图片已插入。');
    } catch (error) {
      setMessage(error instanceof Error ? error.message : '图片上传失败。');
    } finally {
      if (imageInput.current) imageInput.current.value = '';
    }
  }

  if (!editor) return null;
  return <div className="note-editor-screen" role="dialog" aria-modal="true" aria-label="编辑课程笔记">
    <header className="note-editor-header">
      <div><strong>课程笔记</strong><small>{resourceTitle}</small></div>
      <div className="note-editor-actions"><span>{message}</span><button type="button" onClick={save}><Save size={17} />{saving ? '保存中' : '保存'}</button><button type="button" aria-label="关闭编辑器" onClick={onClose}><X size={19} /></button></div>
    </header>
    <div className="note-toolbar" role="toolbar" aria-label="笔记格式工具">
      <ToolButton title="撤销" disabled={!editor.can().undo()} onClick={() => editor.chain().focus().undo().run()}><Undo2 /></ToolButton>
      <ToolButton title="重做" disabled={!editor.can().redo()} onClick={() => editor.chain().focus().redo().run()}><Redo2 /></ToolButton><i />
      <ToolButton title="一级标题" active={editor.isActive('heading', { level: 1 })} onClick={() => editor.chain().focus().toggleHeading({ level: 1 }).run()}><Heading1 /></ToolButton>
      <ToolButton title="二级标题" active={editor.isActive('heading', { level: 2 })} onClick={() => editor.chain().focus().toggleHeading({ level: 2 }).run()}><Heading2 /></ToolButton><i />
      <ToolButton title="粗体" active={editor.isActive('bold')} onClick={() => editor.chain().focus().toggleBold().run()}><Bold /></ToolButton>
      <ToolButton title="斜体" active={editor.isActive('italic')} onClick={() => editor.chain().focus().toggleItalic().run()}><Italic /></ToolButton>
      <ToolButton title="下划线" active={editor.isActive('underline')} onClick={() => editor.chain().focus().toggleUnderline().run()}><UnderlineIcon /></ToolButton>
      <ToolButton title="删除线" active={editor.isActive('strike')} onClick={() => editor.chain().focus().toggleStrike().run()}><Strikethrough /></ToolButton>
      <ToolButton title="高亮" active={editor.isActive('highlight')} onClick={() => editor.chain().focus().toggleHighlight({ color: '#fef08a' }).run()}><Highlighter /></ToolButton>
      <label className="note-color-tool" title="文字颜色"><input type="color" onInput={(event) => editor.chain().focus().setColor((event.target as HTMLInputElement).value).run()} /></label><i />
      <ToolButton title="项目列表" active={editor.isActive('bulletList')} onClick={() => editor.chain().focus().toggleBulletList().run()}><List /></ToolButton>
      <ToolButton title="编号列表" active={editor.isActive('orderedList')} onClick={() => editor.chain().focus().toggleOrderedList().run()}><ListOrdered /></ToolButton>
      <ToolButton title="待办事项" active={editor.isActive('taskList')} onClick={() => editor.chain().focus().toggleTaskList().run()}><CheckSquare /></ToolButton>
      <ToolButton title="引用" active={editor.isActive('blockquote')} onClick={() => editor.chain().focus().toggleBlockquote().run()}><Quote /></ToolButton>
      <ToolButton title="代码块" active={editor.isActive('codeBlock')} onClick={() => editor.chain().focus().toggleCodeBlock().run()}><Code2 /></ToolButton><i />
      <ToolButton title="左对齐" active={editor.isActive({ textAlign: 'left' })} onClick={() => editor.chain().focus().setTextAlign('left').run()}><AlignLeft /></ToolButton>
      <ToolButton title="居中" active={editor.isActive({ textAlign: 'center' })} onClick={() => editor.chain().focus().setTextAlign('center').run()}><AlignCenter /></ToolButton>
      <ToolButton title="右对齐" active={editor.isActive({ textAlign: 'right' })} onClick={() => editor.chain().focus().setTextAlign('right').run()}><AlignRight /></ToolButton>
      <ToolButton title="链接" active={editor.isActive('link')} onClick={setLink}><Link2 /></ToolButton>
      <ToolButton title="插入图片" onClick={() => imageInput.current?.click()}><ImagePlus /></ToolButton>
      <ToolButton title="插入表格" onClick={() => editor.chain().focus().insertTable({ rows: 3, cols: 3, withHeaderRow: true }).run()}><Table2 /></ToolButton>
      <input ref={imageInput} className="sr-only" type="file" accept="image/jpeg,image/png,image/webp" onChange={(event) => uploadImage(event.target.files?.[0])} />
    </div>
    <main className="note-canvas"><article className="note-paper"><input className="note-title-input" value={title} maxLength={120} placeholder="请输入标题" onChange={(event) => setTitle(event.target.value)} /><EditorContent editor={editor} /></article></main>
  </div>;
}
