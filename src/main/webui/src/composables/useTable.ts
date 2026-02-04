import { ref, computed, watch, type Ref, type ComputedRef } from 'vue'

export interface Column<K extends string = string> {
  key: K
  label: string
}

export interface UseTableOptions<K extends string> {
  columns: readonly Column<K>[]
  defaultSortKey?: K
  defaultSortDir?: 'asc' | 'desc'
  defaultPageSize?: number
}

export interface UseTableReturn<T, K extends string> {
  // Data
  allRows: Ref<T[]>

  // Search
  query: Ref<string>

  // Sorting
  sortKey: Ref<K>
  sortDir: Ref<'asc' | 'desc'>
  toggleSort: (key: K) => void

  // Pagination
  currentPage: Ref<number>
  pageSize: Ref<number>
  totalElements: ComputedRef<number>
  totalPages: ComputedRef<number>
  goToPage: (page: number) => void
  changePageSize: (size: number) => void

  // Computed rows
  filteredRows: ComputedRef<T[]>
  sortedRows: ComputedRef<T[]>
  paginatedRows: ComputedRef<T[]>

  // Loading state
  loading: Ref<boolean>
  error: Ref<string | null>

  // Utility
  setData: (data: T[]) => void
}

function safeString(v: unknown): string {
  if (v === null || v === undefined) return ''
  if (typeof v === 'boolean') return v ? 'true' : 'false'
  return String(v)
}

export function useTable<T extends object, K extends string>(
  options: UseTableOptions<K>,
  searchFields: (keyof T)[] = []
): UseTableReturn<T, K> {
  const {
    columns,
    defaultSortKey = columns[0]?.key as K,
    defaultSortDir = 'desc',
    defaultPageSize = 20
  } = options

  // Data
  const allRows = ref<T[]>([]) as Ref<T[]>

  // Search
  const query = ref('')

  // Sorting
  const sortKey = ref<K>(defaultSortKey) as Ref<K>
  const sortDir = ref<'asc' | 'desc'>(defaultSortDir)

  // Pagination
  const currentPage = ref(0)
  const pageSize = ref(defaultPageSize)

  // Loading state
  const loading = ref(true)
  const error = ref<string | null>(null)

  // Filtered rows (search applied)
  const filteredRows = computed(() => {
    if (!query.value || searchFields.length === 0) {
      return allRows.value
    }
    const q = query.value.toLowerCase()
    return allRows.value.filter(row =>
      searchFields.some(field => {
        const val = row[field]
        return typeof val === 'string' && val.toLowerCase().includes(q)
      })
    )
  })

  // Total elements and pages based on filtered results
  const totalElements = computed(() => filteredRows.value.length)
  const totalPages = computed(() => Math.ceil(filteredRows.value.length / pageSize.value) || 1)

  // Sorted rows
  const sortedRows = computed(() => {
    const key = sortKey.value
    const dir = sortDir.value
    const arr = [...filteredRows.value]
    arr.sort((a, b) => {
      const va = (a as Record<string, unknown>)[key]
      const vb = (b as Record<string, unknown>)[key]
      const sa = safeString(va)
      const sb = safeString(vb)
      if (sa < sb) return dir === 'asc' ? -1 : 1
      if (sa > sb) return dir === 'asc' ? 1 : -1
      return 0
    })
    return arr
  })

  // Paginated rows
  const paginatedRows = computed(() => {
    const start = currentPage.value * pageSize.value
    const end = start + pageSize.value
    return sortedRows.value.slice(start, end)
  })

  // Sort toggle
  const toggleSort = (key: K): void => {
    if (sortKey.value === key) {
      sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
    } else {
      sortKey.value = key
      sortDir.value = 'asc'
    }
  }

  // Pagination navigation
  const goToPage = (page: number): void => {
    if (page >= 0 && page < totalPages.value) {
      currentPage.value = page
    }
  }

  const changePageSize = (size: number): void => {
    pageSize.value = size
    currentPage.value = 0
  }

  // Reset pagination when search changes
  watch(query, () => {
    currentPage.value = 0
  })

  // Set data utility
  const setData = (data: T[]): void => {
    allRows.value = data
    currentPage.value = Math.min(
      currentPage.value,
      Math.max(0, Math.ceil(data.length / pageSize.value) - 1)
    )
  }

  return {
    allRows,
    query,
    sortKey,
    sortDir,
    toggleSort,
    currentPage,
    pageSize,
    totalElements,
    totalPages,
    goToPage,
    changePageSize,
    filteredRows,
    sortedRows,
    paginatedRows,
    loading,
    error,
    setData
  }
}
