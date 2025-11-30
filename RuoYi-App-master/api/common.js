import upload from '@/utils/upload'

// 上传单个文件
export function uploadFile(filePath) {
  return upload({
    url: '/common/upload',
    name: 'file',
    filePath: filePath
  })
}

// 上传多个文件
export function uploadFiles(filePaths) {
  const uploadPromises = filePaths.map(filePath => {
    return upload({
      url: '/common/upload',
      name: 'file',
      filePath: filePath
    })
  })
  return Promise.all(uploadPromises)
}

