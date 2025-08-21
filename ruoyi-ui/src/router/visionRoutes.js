import Layout from '@/layout'

// 视力监测分析系统路由
const visionRoutes = [
    {
        path: '/vision',
        component: Layout,
        redirect: 'noRedirect',
        name: 'Vision',
        meta: { title: '视力监测分析系统', icon: 'eye' },
        children: [
            {
                path: 'person',
                component: () => import('@/views/visionrecord/person/index'),
                name: 'Person',
                meta: { title: '人员管理', icon: 'user' },
                permissions: ['vision:person:list']
            },
            {
                path: 'record',
                component: () => import('@/views/visionrecord/visonrecord/index'),
                name: 'VisionRecord',
                meta: { title: '年度视力监测', icon: 'form' },
                permissions: ['vision:record:list']
            },
            {
                path: 'stat',
                component: () => import('@/views/visionrecord/stat/index'),
                name: 'VisionStat',
                meta: { title: '统计与分析', icon: 'chart' },
                permissions: ['vision:stat:view']
            },
            {
                path: 'report',
                component: () => import('@/views/visionrecord/report/index'),
                name: 'VisionReport',
                meta: { title: '年度报告', icon: 'documentation' },
                permissions: ['vision:report:annual']
            }
        ]
    }
]

export default visionRoutes 