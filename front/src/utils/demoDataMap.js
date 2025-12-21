
import cover1 from '@/assets/虚拟主播/视频封面/图像 - 1742412405144.封面.jpg'
import cover2 from '@/assets/虚拟主播/视频封面/图像 - “在这里见到我，很惊讶吗？”.封面.jpg'
import cover3 from '@/assets/虚拟主播/视频封面/图像 - 【live2d模型展示】又是白毛与小猫咪（远古库存版）.封面.jpg'
import cover4 from '@/assets/虚拟主播/视频封面/图像 - 【Live2d模型展示】请问您今天要来点猫猫吗.封面.jpg'
import cover5 from '@/assets/虚拟主播/视频封面/图像 - 【live2d量贩模型】jk社恐小黑猫，适合内向宝宝的可爱日常公皮，支持vb.封面.jpg'
import cover6 from '@/assets/虚拟主播/视频封面/图像 - 所有知名虚拟主播的立牌.封面.jpg'
import cover7 from '@/assets/虚拟主播/视频封面/图像 - 超级简单的虚拟形象直播教程！4分钟教会你添加虚拟人物！.封面.jpg'

import video1 from '@/assets/虚拟主播/视频/video-1.mp4'
import video2 from '@/assets/虚拟主播/视频/video-2.mp4'
import video3 from '@/assets/虚拟主播/视频/video-3.mp4'
import video4 from '@/assets/虚拟主播/视频/video-4.mp4'
import video5 from '@/assets/虚拟主播/视频/video-5.mp4'
import video6 from '@/assets/虚拟主播/视频/video-6.mp4'
import video7 from '@/assets/虚拟主播/视频/video-7.mp4'

const coverMap = {
  'video-1': cover1,
  'video-2': cover2,
  'video-3': cover3,
  'video-4': cover4,
  'video-5': cover5,
  'video-6': cover6,
  'video-7': cover7,
  // Fallback for cover filenames if they exist in DB
  'cover-1': cover1,
  'cover-2': cover2,
  'cover-3': cover3,
  'cover-4': cover4,
  'cover-5': cover5,
  'cover-6': cover6,
  'cover-7': cover7,
}

const videoMap = {
  'video-1': video1,
  'video-2': video2,
  'video-3': video3,
  'video-4': video4,
  'video-5': video5,
  'video-6': video6,
  'video-7': video7,
}

export function getDemoAsset(url, type = 'any') {
  if (!url) return null
  
  // Extract filename or key from URL
  const filename = url.split('/').pop().split('.')[0] // e.g. "video-1" from "video-1.mp4"
  
  if (type === 'video' || url.endsWith('.mp4')) {
    // Try exact match or key match
    if (videoMap[filename]) return videoMap[filename]
    // Check if the url itself is a key
    if (videoMap[url]) return videoMap[url]
  }
  
  if (type === 'image' || url.endsWith('.jpg') || url.endsWith('.png')) {
     if (coverMap[filename]) return coverMap[filename]
  }
  
  // Cross check: if we have a video-X key, return its cover for image requests
  if (type === 'image' && filename.startsWith('video-')) {
      if (coverMap[filename]) return coverMap[filename]
  }

  return null
}
