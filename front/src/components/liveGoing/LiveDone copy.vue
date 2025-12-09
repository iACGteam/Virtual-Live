<template>
  <div class="live-container">

    <!-- 中间：直播预览 -->
    <main class="center-preview">

      <!-- 直播信息 -->
      <div class="info">
        <div class="live-photo">
          封面占位
        </div>
        <div class="title" @click="openSettings">
          直播标题
          <el-icon class="settings" @click="openSettings">
            <Setting />
          </el-icon>
        </div>
      </div>

      <div class="preview-box">
        <div class="obs">
          <el-form :model="form" label-width="auto" style="max-width: 600px" class="obs-form">
            <el-form-item label="服务" class="item">
              <el-input v-model="obs.service" />
            </el-form-item>
            <el-form-item label="服务器">
              <el-input v-model="obs.url" />
            </el-form-item>
            <el-form-item label="推流码">
              <el-input v-model="obs.code" />
            </el-form-item>
          </el-form> 
        </div>
      </div>
    </main>

    <!-- 右侧：互动区 -->
    <aside class="right-panel">
      <div class="panel-section">
        <div class="panel-title">高能用户</div>
        <div class="high-energy">暂无高能用户</div>
      </div>

      <div class="panel-section">
        <div class="panel-title">直播互动</div>
        <div class="chat-box">
          此处显示SC弹幕、礼物、互动消息
        </div>
      </div>

      <div class="chat-input">
        <DanmuArea></DanmuArea>
      </div>
    </aside>



    <!-- 设置弹窗 -->
    <el-dialog v-model="settingsVisible" title="开播信息" width="520px">
      <el-form :model="form" label-width="90px">

        <!-- 封面上传 -->
        <el-form-item label="封面">
          <el-upload class="cover-uploader" action="#" :show-file-list="false" :auto-upload="false"
            :on-change="handleCoverChange">
            <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-preview" />
            <div v-else class="cover-placeholder">
              <el-icon>
                <Plus />
              </el-icon>
              <p>点击上传封面</p>
            </div>
          </el-upload>
        </el-form-item>

        <!-- 分区选择 -->
        <el-form-item label="分区">
          <el-select v-model="form.category" placeholder="选择直播分区">
            <el-option label="娱乐" value="娱乐" />
            <el-option label="游戏" value="游戏" />
            <el-option label="学习" value="学习" />
            <el-option label="科技" value="科技" />
          </el-select>
        </el-form-item>

        <!-- 标题 -->
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入直播标题" />
        </el-form-item>

        <!-- 公告 -->
        <el-form-item label="公告">
          <el-input type="textarea" rows="3" v-model="form.notice" placeholder="直播公告内容" />
        </el-form-item>

        <!-- 观看权限 -->
        <el-form-item label="权限">
          <el-select v-model="form.permission" placeholder="选择观看权限">
            <el-option label="公开" value="public" />
            <el-option label="仅粉丝" value="fans" />
            <el-option label="私密" value="fans" />
          </el-select>
        </el-form-item>

        <!-- 话题选择 -->
        <el-form-item label="话题">
          <el-select v-model="form.topics" multiple filterable collapse-tags placeholder="选择相关话题">
            <el-option label="AI" value="AI" />
            <el-option label="Vtuber" value="Vtuber" />
            <el-option label="二次元" value="二次元" />
            <el-option label="游戏" value="游戏" />
          </el-select>
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="settingsVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSettings">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref } from "vue";
import { Plus, Camera, Monitor, ChromeFilled, MagicStick, Setting } from "@element-plus/icons-vue";
import DanmuArea from "@/components/liveGoing/DanmuArea.vue";

export default {
  components: {
    Plus,
    Camera,
    Monitor,
    ChromeFilled,
    Setting,
    MagicStick,
    DanmuArea,
  },
  data() {
    return {
      settingsVisible: false,
      form: {
        title: "直播标题",
        isPublic: true,
        allowDanmu: true,
      },
      obs: {
        service:"服务",
        url:"1.1.1.1:1111",
        code:"tuiliuma",
      },
      volume: 50,
      msg: "",
    };
  }, methods: {
    openSettings() {
      this.settingsVisible = true;
    },
    saveSettings() {
      console.log("保存设置：", this.form);
      this.settingsVisible = false;
    },
  },
};
</script>

<style scoped>
.live-container {
  display: flex;
  height: 100vh;
  background-color: #181818;
  color: #ddd;
  padding-top: 30px;
}

/* 中间预览 */
.center-preview {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.info {
  margin-left: 10rem;
  gap: 20px;
  width: 100%;
  display: flex;
  flex-direction: row;
  flex: 1;

}

.live-photo {
  height: 90%;
  width: 100px;
  background: white;
  color: black;
}

.title {
  margin-top: 12px;
  color: white;
  font: 1.1rem;
  font-size: larger;
}

.settings {
  color: white;
  padding-top: 1px;
  margin-left: 5px;
  transition: all 0.3s;

}

.settings :hover {
  transform: translateY(-2px);
}

.preview-box {
  width: 90%;
  height: 90%;
  background: gray;
  border-radius: 8px;
  position: relative;
  flex: 9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.obs-form :deep(.el-form-item__label) {
  color: #fff !important;
}

.add-material {
  position: absolute;
  top: 40%;
  left: 45%;
  text-align: center;
  color: white;
}

.preview-actions {
  position: absolute;
  bottom: 80px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 20px;
}

.icon-box {
  padding: 6px 12px;
  background: #444;
  border-radius: 8px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 右侧面板 */
.right-panel {
  width: 280px;
  background-color: #1f1f1f;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-section {
  background: #262626;
  padding: 10px;
  border-radius: 6px;
}

.panel-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 8px;
}

.high-energy,
.chat-box {
  background: #303030;
  border-radius: 6px;
  padding: 12px;
  height: 120px;
  color: #aaa;
}

.chat-input {
  margin-top: auto;
  background-color: #2a2a2a;
}
</style>
