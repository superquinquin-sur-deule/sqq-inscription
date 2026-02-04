export interface FormattingReturn {
  formatDate: (v?: string) => string
  format: (v: unknown) => string
  yesNo: (v?: boolean) => string
  safeString: (v: unknown) => string
}

export function useFormatting(): FormattingReturn {
  const safeString = (v: unknown): string => {
    if (v === null || v === undefined) return ''
    if (typeof v === 'boolean') return v ? 'true' : 'false'
    return String(v)
  }

  const yesNo = (v?: boolean): string => {
    return v ? 'Oui' : 'Non'
  }

  const formatDate = (v?: string): string => {
    if (!v) return '—'
    const date = new Date(v)
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const format = (v: unknown): string => {
    switch (safeString(v)) {
      case 'PAYMENT_PENDING':
        return 'Paiement en attente'
      case 'PAID':
        return 'Payé'
      case 'PROCESSED':
        return 'Traitée'
      case 'ARCHIVED':
        return 'Archivée'
      case 'MADAME':
        return 'Madame'
      case 'MONSIEUR':
        return 'Monsieur'
      default:
        return safeString(v)
    }
  }

  return { formatDate, format, yesNo, safeString }
}
