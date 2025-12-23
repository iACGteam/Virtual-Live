<template>
  <div class="wallet-view">
    <div class="wallet-card">
      <h2>我的钱包</h2>
      <div class="balance-section">
        <span class="label">当前余额</span>
        <div class="amount">¥ {{ balance }}</div>
      </div>
      
      <div class="recharge-options">
        <h3>充值金额</h3>
        <div class="options-grid">
            <div 
                v-for="amount in options" 
                :key="amount" 
                class="option-item"
                :class="{ active: selectedAmount === amount }"
                @click="selectedAmount = amount"
            >
                ¥ {{ amount }}
            </div>
        </div>
      </div>

      <div class="actions">
        <el-button type="primary" size="large" @click="handleRecharge" :loading="loading">立即充值</el-button>
        <el-button size="large" @click="$router.go(-1)">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getAuthToken } from "@/utils/auth";
import { ElMessage } from "element-plus";

export default {
  name: "WalletView",
  data() {
    return {
      balance: 0,
      options: [6, 30, 98, 198, 328, 648],
      selectedAmount: 6,
      loading: false
    };
  },
  mounted() {
    this.fetchBalance();
  },
  methods: {
    async fetchBalance() {
        const token = getAuthToken();
        if (!token) return;
        try {
            const res = await fetch("/api/v1/live/user/me", {
                headers: { "Authorization": "Bearer " + token }
            });
            const json = await res.json();
            if (json.code === 0 || json.code === 200) {
                this.balance = json.data.balance || 0;
            }
        } catch (e) {
            console.error(e);
        }
    },
    async handleRecharge() {
        this.loading = true;
        const token = getAuthToken();
        try {
            const res = await fetch("/api/v1/live/user/wallet/recharge", {
                method: "POST",
                headers: { 
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ amount: this.selectedAmount })
            });
            
            const json = await res.json();
            if (json.code === 0 || json.code === 200) {
                ElMessage.success(`充值 ¥${this.selectedAmount} 成功`);
                // 更新余额
                this.balance = json.data; 
            } else {
                ElMessage.error(json.message || "充值失败");
            }
        } catch (e) {
            console.error(e);
            ElMessage.error("充值失败");
        } finally {
            this.loading = false;
        }
    }
  }
};
</script>

<style scoped>
.wallet-view {
  min-height: 100vh;
  background: #f5f7fa;
  display: flex;
  justify-content: center;
  padding-top: 50px;
}

.wallet-card {
  width: 600px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  height: fit-content;
}

h2 {
  margin-bottom: 30px;
  text-align: center;
  color: #333;
}

.balance-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.label {
  color: #666;
  font-size: 14px;
}

.amount {
  font-size: 36px;
  font-weight: bold;
  color: #ff69b4;
  margin-top: 10px;
}

.recharge-options h3 {
  margin-bottom: 20px;
  font-size: 16px;
  color: #333;
}

.options-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-bottom: 40px;
}

.option-item {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 20px 0;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 18px;
  color: #606266;
}

.option-item:hover {
  border-color: #ff69b4;
  color: #ff69b4;
}

.option-item.active {
  background: #ff69b4;
  border-color: #ff69b4;
  color: #fff;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.actions .el-button {
  width: 100%;
  margin: 0;
}
</style>