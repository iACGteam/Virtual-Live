<template>
    <div class="search-input">
        <div class="search-field">
            <span class="search-icon">🔍</span>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索作品名或用户名..."
              @keyup.enter="handleSearch"
            >
        </div>
        <span class="search-divider"></span>
        <button class="search-btn" @click="handleSearch">搜索</button>
    </div>
</template>

<script>
    export default{
        data(){

        },
        methods: {
            handleSearch() {
            // 搜索功能通过 v-model 和计算属性自动实现
            // 这里可以添加额外的搜索逻辑，如搜索历史记录等
            }
        }
    }
    
</script>
<style>
.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.search-input {
  flex: 1;
  max-width: none;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.12);
  overflow: hidden;
  transition: border-color 0.3s ease, background-color 0.3s ease;
}

.search-input:hover {
  border-color: rgba(255, 255, 255, 0.25);
  background-color: rgba(255, 255, 255, 0.08);
}

.search-input:focus-within {
  border-color: rgba(255, 255, 255, 0.3);
  background-color: rgba(255, 255, 255, 0.08);
}

.search-field {
  display: flex;
  align-items: center;
  flex: 1;
  gap: 12px;
  padding: 0 10px;
}

.search-field input {
  flex: 1;
  border: none;
  background: transparent;
  color: #fff;
  font-size: 0.95rem;
}

.search-field input:focus {
  outline: none;
}

.search-field input::placeholder {
  color: rgba(255, 255, 255, 0.55);
}

.search-icon {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.7);
}

.search-divider {
  width: 1px;
  height: 24px;
  background: rgba(255, 255, 255, 0.15);
}

.search-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 0 12px 12px 0;
  background: rgba(255, 255, 255, 0.07);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  border-left: 1px solid rgba(255, 255, 255, 0.15);
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: flex-end;
}
</style>
