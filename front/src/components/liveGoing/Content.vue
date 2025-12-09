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

    const startLive = () => {
      console.log("开直播信息:", {
        title: title.value,
        cover: coverUrl.value,
        category: category.value,
        isPublic: isPublic.value,
        allowDanmu: allowDanmu.value,
      });

      // 跳转路由
      router.push("/live-manage");
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
