interface SuccessResult<T> {
  isSuccess: true;
  data: T;
}

interface FailureResult {
  isSuccess: false;
  error: string;
}

/**
 * API result model
 */
export type Result<T = unknown> = SuccessResult<T> | FailureResult;
