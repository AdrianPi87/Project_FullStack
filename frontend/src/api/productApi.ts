import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import type {
  AnalyticsEventRequest,
  AnalyticsEventResponse,
  AuthResponse,
  CartItemRequest,
  CartResponse,
  ConfigurationRequest,
  ConfigurationResponse,
  LoginRequest,
  OrderResponse,
  ProductDetail,
  ProductSummary,
  RegisterRequest,
  UserResponse,
} from '../types'

export const productApi = createApi({
  reducerPath: 'productApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api',
    prepareHeaders: (headers) => {
      const token = localStorage.getItem('configurator.token')
      if (token) headers.set('Authorization', `Bearer ${token}`)
      return headers
    },
  }),
  tagTypes: ['Product', 'Cart', 'Order'],
  endpoints: (builder) => ({
    register: builder.mutation<AuthResponse, RegisterRequest>({
      query: (body) => ({ url: '/auth/register', method: 'POST', body }),
    }),
    login: builder.mutation<AuthResponse, LoginRequest>({
      query: (body) => ({ url: '/auth/login', method: 'POST', body }),
    }),
    getAdminUsers: builder.query<UserResponse[], void>({
      query: () => '/admin/users',
    }),
    getProducts: builder.query<ProductSummary[], void>({
      query: () => '/products',
      providesTags: ['Product'],
    }),
    getProduct: builder.query<ProductDetail, number>({
      query: (id) => `/products/${id}`,
    }),
    configure: builder.mutation<ConfigurationResponse, ConfigurationRequest>({
      query: (body) => ({ url: '/configurations', method: 'POST', body }),
    }),
    getCart: builder.query<CartResponse, string>({
      query: (sessionId) => `/cart?sessionId=${sessionId}`,
      providesTags: ['Cart'],
    }),
    addToCart: builder.mutation<CartResponse, CartItemRequest>({
      query: (body) => ({ url: '/cart/items', method: 'POST', body }),
      invalidatesTags: ['Cart'],
    }),
    updateCartItem: builder.mutation<CartResponse, { sessionId: string; itemId: string; quantity: number }>({
      query: ({ sessionId, itemId, quantity }) => ({
        url: `/cart/items/${itemId}?sessionId=${sessionId}`,
        method: 'PATCH',
        body: { quantity },
      }),
      invalidatesTags: ['Cart'],
    }),
    removeCartItem: builder.mutation<CartResponse, { sessionId: string; itemId: string }>({
      query: ({ sessionId, itemId }) => ({
        url: `/cart/items/${itemId}?sessionId=${sessionId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Cart'],
    }),
    mergeCarts: builder.mutation<CartResponse, { sourceSessionId: string; targetSessionId: string }>({
      query: (body) => ({ url: '/cart/merge', method: 'POST', body }),
      invalidatesTags: ['Cart'],
    }),
    checkout: builder.mutation<OrderResponse, string>({
      query: (sessionId) => ({ url: '/orders', method: 'POST', body: { sessionId } }),
      invalidatesTags: ['Cart', 'Order'],
    }),
    getOrders: builder.query<OrderResponse[], string>({
      query: (sessionId) => `/orders?sessionId=${sessionId}`,
      providesTags: ['Order'],
    }),
    recordEvent: builder.mutation<AnalyticsEventResponse, AnalyticsEventRequest>({
      query: (body) => ({ url: '/analytics/events', method: 'POST', body }),
    }),
  }),
})

export const {
  useRegisterMutation,
  useLoginMutation,
  useGetAdminUsersQuery,
  useGetProductsQuery,
  useGetProductQuery,
  useConfigureMutation,
  useGetCartQuery,
  useAddToCartMutation,
  useUpdateCartItemMutation,
  useRemoveCartItemMutation,
  useMergeCartsMutation,
  useCheckoutMutation,
  useGetOrdersQuery,
  useRecordEventMutation,
} = productApi