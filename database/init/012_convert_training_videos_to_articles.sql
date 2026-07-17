-- The current demo contains article material only. Normalize any legacy video
-- resource so every published course is rendered and managed as an article.
UPDATE training_resource
SET resource_type = 'ARTICLE',
    storage_mode = 'TEXT',
    content = CASE
      WHEN content IS NULL OR TRIM(content) = '' THEN CONCAT(
        '<h2>', title, '</h2>',
        '<p>本课程介绍照护服务前的风险核对、环境检查与规范操作要求。</p>',
        '<h3>学习重点</h3>',
        '<ul><li>识别老人当前状态与环境风险。</li>',
        '<li>按照培训流程完成安全照护操作。</li>',
        '<li>发现异常后及时处置、记录并上报。</li></ul>'
      )
      ELSE content
    END,
    summary = REPLACE(REPLACE(COALESCE(summary, CONCAT(title, '培训文章')), '演示视频链接', '培训文章'), '培训视频', '培训文章'),
    external_url = NULL,
    updated_at = CURRENT_TIMESTAMP
WHERE resource_type = 'VIDEO' OR storage_mode = 'EXTERNAL_LINK';
