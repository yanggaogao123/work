/**
 * 打卡状态
 * @type {{LEAVE_EARLY: {text: string, value: string}, LATE: {text: string, value: string}, ABSENT: {text: string, value: string}, NO_SIGN: {text: string, value: string}, NORMAL: {text: string, value: string}}}
 */
export const SignStatusEnum = {
  NO_SIGN: { value: '0', text: '缺卡' },
  NORMAL: { value: '1', text: '正常' },
  LATE: { value: '2', text: '迟到' },
  ABSENT: { value: '3', text: '旷工' },
  LEAVE_EARLY: { value: '4', text: '早退' }
}

/**
 * 今日打卡状态
 * @type {{NO: {text: string, value: number}, NEED: {text: string, value: number}, TO_DAY_NO_NEED: {text: string, value: number}, USER_NO_NEED: {text: string, value: number}, NO_RULE: {text: string, value: number}, OK: {text: string, label: string, value: number}, OFF: {text: string, label: string, value: number}, ON: {text: string, label: string, value: number}}}
 */
export const SignToDayStatusEnum = {
  TO_DAY_NO_NEED: { value: -3, text: '今天休息，无需打卡' },
  USER_NO_NEED: { value: -2, text: '您无需打卡' },
  NO_RULE: { value: -1, text: '未设置考勤规则，无法打卡' },
  NEED: { value: 0, text: '今天需要打卡' },
  NO: { value: 1, text: '今天还没有打卡' },
  ON: { value: 2, text: '今天只打了上班卡', label: '上班打卡' },
  OFF: { value: 3, text: '今天只打了下班卡', label: '下班打卡' },
  OK: { value: 4, text: '今日打卡已完成', label: '今日打卡已完成' }
}

/**
 * 打卡类型（补卡类型）
 * @type {{OFF_WORK: {text: string, value: string}, ON_WORK: {text: string, value: string}}}
 */
export const SignTypeEnum = {
  ON_WORK: { value: '1', text: '上班卡' },
  OFF_WORK: { value: '2', text: '下班卡' },
  OUT_WORK: { value: '3', text: '外出卡' },
  LEAVE: { value: '4', text: '请假' },
  BUSINESS_TRAVEL: { value: '5', text: '出差' },
}