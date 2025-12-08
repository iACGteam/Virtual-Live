const STORAGE_KEY = 'virtual-live-mock-users'

const defaultUsers = [
  {
    username: 'admin',
    email: 'admin@virtual.live',
    password: 'admin123',
    name: '系统管理员'
  },
  {
    username: 'demo',
    email: 'demo@virtual.live',
    password: 'user123',
    name: '演示用户'
  }
]

function loadMockUsers() {
  if (typeof window === 'undefined' || !window.localStorage) {
    return defaultUsers.map(user => ({ ...user }))
  }

  try {
    const cached = window.localStorage.getItem(STORAGE_KEY)
    if (cached) {
      const parsed = JSON.parse(cached)
      if (Array.isArray(parsed) && parsed.length) {
        return parsed
      }
    }
  } catch (error) {
    console.warn('[mockUsers] Failed to read from localStorage:', error)
  }

  return defaultUsers.map(user => ({ ...user }))
}

function persistMockUsers(users) {
  if (typeof window === 'undefined' || !window.localStorage) return
  try {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(users))
  } catch (error) {
    console.warn('[mockUsers] Failed to persist to localStorage:', error)
  }
}

let mockUsers = loadMockUsers()

// 重新加载用户数据，确保获取最新的数据（包括新注册的用户和密码）
function reloadMockUsers() {
  mockUsers = loadMockUsers()
}

export function findMockUser(username, password) {
  // 每次查找前重新加载，确保获取最新的用户数据（包括密码）
  reloadMockUsers()
  const normalizedUsername = String(username || '').trim().toLowerCase()
  const normalizedPassword = String(password || '')
  return mockUsers.find(user => {
    const usernameHit = user.username.toLowerCase() === normalizedUsername
    const emailHit = typeof user.email === 'string' && user.email.toLowerCase() === normalizedUsername
    return (usernameHit || emailHit) && user.password === normalizedPassword
  })
}

export function listMockUsers() {
  return mockUsers.map(user => {
    const safeUser = { ...user }
    delete safeUser.password
    return safeUser
  })
}

export function isMockUsernameTaken(username) {
  reloadMockUsers()
  const normalized = String(username || '').trim().toLowerCase()
  if (!normalized) return false
  return mockUsers.some(user => user.username.toLowerCase() === normalized)
}

export function isMockEmailTaken(email) {
  reloadMockUsers()
  const normalized = String(email || '').trim().toLowerCase()
  if (!normalized) return false
  return mockUsers.some(user => typeof user.email === 'string' && user.email.toLowerCase() === normalized)
}

export function addMockUser({ username, email, password, name }) {
  reloadMockUsers() // 确保使用最新的用户列表
  const normalizedUsername = String(username || '').trim()
  const normalizedEmail = String(email || '').trim()
  const normalizedPassword = String(password || '')

  if (!normalizedUsername || !normalizedEmail || !normalizedPassword) {
    return null
  }

  const newUser = {
    username: normalizedUsername,
    email: normalizedEmail.toLowerCase(),
    password: normalizedPassword, // 密码被明确保存
    name: name || normalizedUsername
  }

  mockUsers.push(newUser)
  persistMockUsers(mockUsers) // 保存到 localStorage，包括密码
  return newUser
}

// 更新用户密码
export function updateMockUserPassword(username, newPassword) {
  reloadMockUsers() // 确保使用最新的用户列表
  const normalizedUsername = String(username || '').trim().toLowerCase()
  const normalizedPassword = String(newPassword || '')

  if (!normalizedUsername || !normalizedPassword) {
    return false
  }

  const userIndex = mockUsers.findIndex(user => {
    const usernameHit = user.username.toLowerCase() === normalizedUsername
    const emailHit = typeof user.email === 'string' && user.email.toLowerCase() === normalizedUsername
    return usernameHit || emailHit
  })

  if (userIndex === -1) {
    return false
  }

  mockUsers[userIndex].password = normalizedPassword
  persistMockUsers(mockUsers) // 保存到 localStorage
  return true
}


