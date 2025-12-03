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
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 105, 180, 0.3);
  overflow: hidden;
  transition: border-color 0.3s ease, background-color 0.3s ease, box-shadow 0.3s ease;
}

.search-input:hover {
  border-color: rgba(255, 105, 180, 0.5);
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 12px rgba(255, 105, 180, 0.15);
}

.search-input:focus-within {
  border-color: rgba(255, 105, 180, 0.6);
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 16px rgba(255, 105, 180, 0.2);
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
  color: #2d2d2d;
  font-size: 0.95rem;
}

.search-field input:focus {
  outline: none;
}

.search-field input::placeholder {
  color: rgba(45, 45, 45, 0.5);
}

.search-icon {
  font-size: 1.1rem;
  color: rgba(45, 45, 45, 0.6);
}

.search-divider {
  width: 1px;
  height: 24px;
  background: rgba(255, 105, 180, 0.2);
}

.search-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 0 12px 12px 0;
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  border-left: 1px solid rgba(255, 105, 180, 0.3);
  transition: all 0.3s ease;
}

.search-btn:hover {
  background: linear-gradient(135deg, #ff7bc4 0%, #a085e8 50%, #5ee0d5 100%);
  box-shadow: 0 4px 16px rgba(255, 105, 180, 0.3);
  transform: translateY(-1px);
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: flex-end;
}

</style>
