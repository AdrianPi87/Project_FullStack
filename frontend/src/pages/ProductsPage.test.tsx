/// <reference types="@testing-library/jest-dom/vitest" />
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it, vi } from 'vitest'
import ProductsPage from './ProductsPage'

vi.mock('../api/productApi', () => ({
  useGetProductsQuery: () => ({
    data: [
      {
        id: 1,
        name: 'Custom Laptop Pro',
        slug: 'custom-laptop-pro',
        description: 'Configure laptop',
        basePrice: 899,
        active: true,
      },
    ],
    isLoading: false,
    error: undefined,
  }),
}))

describe('ProductsPage', () => {
  it('renders products', () => {
    render(
      <MemoryRouter>
        <ProductsPage />
      </MemoryRouter>,
    )
    expect(screen.getByText('Custom Laptop Pro')).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /Configure/i })).toBeInTheDocument()
  })
})
