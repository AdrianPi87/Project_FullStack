export interface OptionResponse {
  id: number
  name: string
  description?: string
  sku?: string
  priceDelta: number
  stock: number
  imageUrl?: string
  active: boolean
  defaultOption: boolean
  sortOrder: number
}

export interface OptionGroupResponse {
  id: number
  name: string
  displayName: string
  selectionType: 'single' | 'multi'
  required: boolean
  minSelections: number
  maxSelections: number
  sortOrder: number
  options: OptionResponse[]
}

export interface ProductSummary {
  id: number
  name: string
  slug: string
  description?: string
  category?: string
  imageUrl?: string
  basePrice: number
  active: boolean
}

export interface ProductDetail extends ProductSummary {
  optionGroups: OptionGroupResponse[]
}

export interface PriceLine {
  label: string
  amount: number
}

export interface ConfigurationResponse {
  id: string
  productId: number
  selectedOptionIds: number[]
  totalPrice: number
  status: string
  priceBreakdown: PriceLine[]
}

export interface ConfigurationRequest {
  productId: number
  selectedOptionIds: number[]
  sessionId?: string
}

export interface CartItemResponse {
  id: string
  productId: number
  productName: string
  selectedOptionIds: number[]
  quantity: number
  unitPrice: number
  lineTotal: number
}

export interface CartResponse {
  sessionId: string
  items: CartItemResponse[]
  total: number
}

export interface CartItemRequest {
  sessionId: string
  productId: number
  selectedOptionIds: number[]
  quantity?: number
}

export interface OrderItemResponse {
  id: string
  productId: number
  productName: string
  selectedOptionIds: number[]
  quantity: number
  unitPrice: number
  lineTotal: number
}

export interface OrderResponse {
  id: string
  sessionId: string
  items: OrderItemResponse[]
  total: number
  status: string
  createdAt: string
}

export interface AnalyticsEventRequest {
  type: string
  entityId?: string
  payload?: string
  sessionId?: string
}

export interface AnalyticsEventResponse {
  id: string
  type: string
  entityId?: string
  payload?: string
  sessionId?: string
  createdAt: string
}

export interface RegisterRequest {
  email: string
  password: string
  displayName?: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface AuthResponse {
  token: string
  userId: number
  email: string
  role: string
}

export interface UserResponse {
  id: number
  email: string
  displayName?: string
  role: string
  createdAt: string
}
