export class SortUtils {
  private constructor() {}

  static parseSortProperty(sortField?: string | null, sortOrder?: number | null): string {
    if (!sortOrder) {
      sortOrder = 1;
    }

    if (!sortField) {
      sortField = '';
    }

    return sortOrder <= -1 ? `-${sortField}` : sortField;
  }
}
