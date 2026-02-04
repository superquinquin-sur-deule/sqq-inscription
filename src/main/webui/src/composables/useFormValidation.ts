export interface ValidationRules {
  isEmail: (value: string) => boolean
  isPhone: (value: string) => boolean
  isPostalCode: (value: string) => boolean
  isRequired: (value: string | undefined | null) => boolean
}

export function useFormValidation(): ValidationRules {
  const isEmail = (v: string): boolean => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)
  }

  const isPhone = (v: string): boolean => {
    return /^[+]?([0-9]?[\s\-.]?){6,15}[0-9]$/.test(v)
  }

  const isPostalCode = (v: string): boolean => {
    return /^\d{5}$/.test(v)
  }

  const isRequired = (v: string | undefined | null): boolean => {
    return v !== undefined && v !== null && v.trim().length > 0
  }

  return { isEmail, isPhone, isPostalCode, isRequired }
}
