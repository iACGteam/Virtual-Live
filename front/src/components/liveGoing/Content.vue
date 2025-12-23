<template>
  <div class="start-live-page">
    <h2 class="page-title">🎥 开始直播</h2>

 
    <div class="form-group">
      <label>直播标题</label>
      <el-input v-model="title" placeholder="请输入直播标题..." />
    </div>


    <div class="form-group">
      <label>封面图</label>
      <el-upload
        class="cover-upload"
        action="" 
        :show-file-list="false"
        :on-change="handleCoverChange"
      >
        <div class="upload-box">
          <img v-if="coverUrl" :src="coverUrl" class="cover-preview" />
          <div v-else class="upload-placeholder">点击上传封面</div>
        </div>
      </el-upload>
    </div>


    <div class="form-group">
      <label>直播分类</label>
      <el-select v-model="category" placeholder="选择分类" class="full-width">
        <el-option label="音乐" value="music"></el-option>
        <el-option label="舞蹈" value="dance"></el-option>
        <el-option label="游戏" value="game"></el-option>
        <el-option label="科技" value="tech"></el-option>
        <el-option label="美食" value="food"></el-option>
      </el-select>
    </div>

    <div class="form-group">
      <label>虚拟形象选择</label>
      <el-select v-model="category" placeholder="选择分类" class="full-width">
        <el-option label="音乐" value="music"></el-option>
        <el-option label="舞蹈" value="dance"></el-option>
        <el-option label="游戏" value="game"></el-option>
        <el-option label="科技" value="tech"></el-option>
        <el-option label="美食" value="food"></el-option>
      </el-select>
    </div>


    <div class="form-group settings">
      <el-checkbox v-model="isPublic">公开直播</el-checkbox>
      <el-checkbox v-model="allowDanmu">允许弹幕</el-checkbox>
    </div>

    <div class="form-group">
      <el-button type="primary" size="large" @click="startLive">🚀 开始直播</el-button>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { getAuthToken } from "@/utils/auth";
import { ElMessage } from "element-plus";

export default {
  setup() {
    const title = ref("");
    const coverUrl = ref("");
    const category = ref("");
    const isPublic = ref(true);
    const allowDanmu = ref(true);

    const router = useRouter();

    const handleCoverChange = (file) => {
      const reader = new FileReader();
      reader.onload = (e) => (coverUrl.value = e.target.result);
      reader.readAsDataURL(file.raw);
    };

    const startLive = async () => {
      console.log("开直播信息:", {
        title: title.value,
        cover: coverUrl.value,
        category: category.value,
        isPublic: isPublic.value,
        allowDanmu: allowDanmu.value,
      });

      const token = getAuthToken();
      if (!token) {
        ElMessage.warning("请先登录");
        router.push("/login");
        return;
      }

      const headers = {
        "Content-Type": "application/json",
        "Authorization": token.startsWith("Bearer ") ? token : "Bearer " + token
      };

      try {
        // 1. 获取/创建房间
        const res1 = await fetch("/api/v1/live/rooms/my", { headers });
        if (!res1.ok) {
           ElMessage.error("获取房间信息失败");
           return;
        }
        const data1 = await res1.json();
        // 兼容直接返回对象或 {code:0, data:{...}}
        let roomId = data1.roomId;
        if (!roomId && data1.data && data1.data.roomId) {
            roomId = data1.data.roomId;
        }

        if (!roomId) {
            ElMessage.error("无法获取房间ID");
            return;
        }

        // 2. 更新房间信息
        const updateBody = {
            title: title.value,
            coverUrl: coverUrl.value,
            category: category.value,
            description: title.value // 暂时用标题作为简介
        };

        const res2 = await fetch(`/api/v1/live/rooms/${roomId}/manager/update`, {
            method: "POST",
            headers,
            body: JSON.stringify(updateBody)
        });

        if (!res2.ok) {
            ElMessage.error("更新直播间信息失败");
            return;
        }

        ElMessage.success("直播间准备就绪");
        // 跳转路由
        router.push("/live-manage");

      } catch (error) {
          console.error("Start Live Error:", error);
          ElMessage.error("系统错误，请稍后重试");
      }
    };

    return {
      title,
      coverUrl,
      category,
      isPublic,
      allowDanmu,
      handleCoverChange,
      startLive,
    };
  },
};
</script>

<style scoped>
.start-live-page {
  background: rgba(255, 255, 255, 0.9);
  padding: 40px;
  border-radius: 12px;
  color: #2d2d2d;
  max-width: 600px;
  margin: 20px auto;
  box-shadow: 0 8px 24px rgba(255, 105, 180, 0.18);
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 30px;
  text-align: center;
  color: #ff69b4;
}

.form-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label {
  font-size: 14px;
  color: rgba(45, 45, 45, 0.65);
}

.full-width {
  width: 100%;
}


.cover-upload .upload-box {
  width: 100%;
  height: 200px;
  border: 2px dashed rgba(255, 105, 180, 0.4);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(45, 45, 45, 0.55);
  cursor: pointer;
  transition: 0.2s;
}
.cover-upload .upload-box:hover {
  border-color: #ff69b4;
  color: #ff69b4;
}
.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}


.settings {
  display: flex;
  gap: 20px;
}


.el-button--primary {
  background: #ffffff;
  border: 1px solid rgba(255, 105, 180, 0.5);
  width: 100%;
  font-size: 16px;
  height: 48px;
  color: #ff69b4;
}
.el-button--primary:hover {
  background: rgba(255, 105, 180, 0.06);
  border-color: rgba(255, 105, 180, 0.8);
  color: #ff69b4;
}
</style>
