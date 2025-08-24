<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>年度视力统计</span>
        <div style="float: right; margin-right: 20px;">
          <el-select v-model="queryParams.year" placeholder="请选择年份" @change="handleYearChange">
            <el-option
              v-for="year in yearOptions"
              :key="year"
              :label="year + '年'"
              :value="year"
            />
          </el-select>
        </div>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-title">总检测人数</div>
            <div class="stat-value">{{ annualStat.total || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-title">平均视力(左/右)</div>
            <div class="stat-value">{{ annualStat.avgLeft || 0 }} / {{ annualStat.avgRight || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-title">视力正常率</div>
            <div class="stat-value">{{ formatRate(annualStat.normalRate) }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-title">近视率</div>
            <div class="stat-value">{{ formatRate(annualStat.myopiaRate) }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>视力分布</span>
        <div style="float: right; margin-right: 20px;">
          <el-radio-group v-model="chartType" @change="handleChartTypeChange">
            <el-radio-button label="pie">饼图</el-radio-button>
            <el-radio-button label="bar">柱状图</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <div class="chart-container">
        <div id="distribution-chart" style="width: 100%; height: 400px;"></div>
      </div>
    </el-card>

    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>年度趋势对比</span>
        <div style="float: right; margin-right: 20px;">
          <el-button size="mini" type="primary" @click="handleSetTrendYears">设置年份范围</el-button>
        </div>
      </div>
      <div class="chart-container">
        <div id="trend-chart" style="width: 100%; height: 400px;"></div>
      </div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>性别对比分析</span>
          </div>
          <div class="chart-container">
            <div id="gender-chart" style="width: 100%; height: 400px;"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>年龄段对比分析</span>
          </div>
          <div class="chart-container">
            <div id="age-chart" style="width: 100%; height: 400px;"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>智能分析报告</span>
        <div style="float: right; margin-right: 20px;">
          <el-button size="mini" type="primary" @click="generateAiReport" :loading="aiReportLoading">生成报告</el-button>
        </div>
      </div>
      <div class="ai-report">
        <div v-if="aiReport" v-html="formatAiReport(aiReport)"></div>
        <div v-else class="empty-report">点击"生成报告"按钮获取AI分析报告</div>
      </div>
    </el-card>

    <!-- 设置年份范围对话框 -->
    <el-dialog title="设置年份范围" :visible.sync="yearRangeDialogVisible" width="30%">
      <el-form :model="trendYearRange" label-width="80px">
        <el-form-item label="开始年份">
          <el-select v-model="trendYearRange.startYear" placeholder="请选择开始年份">
            <el-option
              v-for="year in yearOptions"
              :key="year"
              :label="year + '年'"
              :value="year"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="结束年份">
          <el-select v-model="trendYearRange.endYear" placeholder="请选择结束年份">
            <el-option
              v-for="year in yearOptions"
              :key="year"
              :label="year + '年'"
              :value="year"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="yearRangeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmTrendYears">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { getAnnualStat, getTrendStat, getGenderCompareStat, getAgeGroupCompareStat, getAiAnalysisReport, exportStatExcel, exportStatPdf } from "@/api/visionrecord/stat";

export default {
  name: "VisionStat",
  data() {
    const currentYear = new Date().getFullYear();
    return {
      // 查询参数
      queryParams: {
        year: currentYear
      },
      // 年份选项
      yearOptions: Array.from({ length: 10 }, (_, i) => currentYear - i),
      // 年度统计数据
      annualStat: {},
      // 趋势数据
      trendData: [],
      // 性别对比数据
      genderData: [],
      // 年龄段对比数据
      ageGroupData: [],
      // AI分析报告
      aiReport: '',
      aiReportLoading: false,
      // 图表类型（饼图/柱状图）
      chartType: 'pie',
      // 图表实例
      distributionChart: null,
      trendChart: null,
      genderChart: null,
      ageChart: null,
      // 年份范围对话框
      yearRangeDialogVisible: false,
      trendYearRange: {
        startYear: currentYear - 4,
        endYear: currentYear
      }
    };
  },
  created() {
    this.getStatData();
  },
  mounted() {
    // 监听窗口大小变化，调整图表大小
    window.addEventListener('resize', this.resizeCharts);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts);
    // 销毁图表实例
    this.distributionChart && this.distributionChart.dispose();
    this.trendChart && this.trendChart.dispose();
    this.genderChart && this.genderChart.dispose();
    this.ageChart && this.ageChart.dispose();
  },
  methods: {
    // 获取统计数据
    getStatData() {
      this.getAnnualStat();
      this.getTrendStat();
      this.getGenderCompareStat();
      this.getAgeGroupCompareStat();
    },
    // 获取年度统计数据
    getAnnualStat() {
      getAnnualStat(this.queryParams.year).then(response => {
        this.annualStat = response.data;
        this.$nextTick(() => {
          this.initDistributionChart();
        });
      });
    },
    // 获取趋势数据
    getTrendStat() {
      const query = {
        startYear: this.trendYearRange.startYear,
        endYear: this.trendYearRange.endYear
      };
      getTrendStat(query).then(response => {
        this.trendData = response.data;
        this.$nextTick(() => {
          this.initTrendChart();
        });
      });
    },
    // 获取性别对比数据
    getGenderCompareStat() {
      getGenderCompareStat(this.queryParams.year).then(response => {
        this.genderData = response.data;
        this.$nextTick(() => {
          this.initGenderChart();
        });
      });
    },
    // 获取年龄段对比数据
    getAgeGroupCompareStat() {
      getAgeGroupCompareStat(this.queryParams.year).then(response => {
        this.ageGroupData = response.data;
        this.$nextTick(() => {
          this.initAgeChart();
        });
      });
    },
    // 生成AI分析报告
    generateAiReport() {
      this.aiReportLoading = true;
      getAiAnalysisReport(this.queryParams.year).then(response => {
        this.aiReport = response.data;
        this.aiReportLoading = false;
      }).catch(() => {
        this.aiReportLoading = false;
      });
    },
    // 导出Excel
    exportExcel() {
      this.$confirm('是否确认导出当前年度统计数据?', "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        exportStatExcel(this.queryParams.year).then(response => {
          this.downloadFile(response, `视力统计数据_${this.queryParams.year}.xlsx`);
        });
      });
    },
    // 导出PDF
    exportPdf() {
      this.$confirm('是否确认导出当前年度统计图表?', "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        exportStatPdf(this.queryParams.year).then(response => {
          this.downloadFile(response, `视力统计图表_${this.queryParams.year}.pdf`);
        });
      });
    },
    // 下载文件
    downloadFile(response, fileName) {
      const blob = new Blob([response.data]);
      if ('download' in document.createElement('a')) {
        const link = document.createElement('a');
        link.download = fileName;
        link.style.display = 'none';
        link.href = URL.createObjectURL(blob);
        document.body.appendChild(link);
        link.click();
        URL.revokeObjectURL(link.href);
        document.body.removeChild(link);
      } else {
        navigator.msSaveBlob(blob, fileName);
      }
    },
    // 初始化视力分布图表
    initDistributionChart() {
      if (!this.annualStat) return;

      const chartDom = document.getElementById('distribution-chart');
      if (!chartDom) return;

      this.distributionChart = echarts.init(chartDom);

      const data = [
        { name: '正常', value: this.annualStat.normalRate || 0 },
        { name: '轻度', value: this.annualStat.mildRate || 0 },
        { name: '中度', value: this.annualStat.moderateRate || 0 },
        { name: '重度', value: this.annualStat.highRate || 0 }
      ];

      // 转换为百分比
      data.forEach(item => {
        item.value = parseFloat((item.value * 100).toFixed(2));
      });

      let option;

      if (this.chartType === 'pie') {
        option = {
          title: {
            text: `${this.queryParams.year}年视力分布`,
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c}%'
          },
          legend: {
            orient: 'horizontal',
            bottom: 10,
            data: ['正常', '轻度', '中度', '重度']
          },
          series: [
            {
              name: '视力分布',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
              },
              label: {
                show: false,
                position: 'center'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '18',
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: false
              },
              data: data
            }
          ],
          color: ['#67C23A', '#E6A23C', '#409EFF', '#F56C6C']
        };
      } else {
        option = {
          title: {
            text: `${this.queryParams.year}年视力分布`,
            left: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            },
            formatter: '{b}: {c}%'
          },
          xAxis: {
            type: 'category',
            data: ['正常', '轻度', '中度', '重度']
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              formatter: '{value}%'
            }
          },
          series: [
            {
              data: data.map(item => item.value),
              type: 'bar',
              itemStyle: {
                color: function(params) {
                  const colors = ['#67C23A', '#E6A23C', '#409EFF', '#F56C6C'];
                  return colors[params.dataIndex];
                }
              },
              label: {
                show: true,
                position: 'top',
                formatter: '{c}%'
              }
            }
          ]
        };
      }

      this.distributionChart.setOption(option);
    },
    // 初始化趋势图表
    initTrendChart() {
      if (!this.trendData || this.trendData.length === 0) return;

      const chartDom = document.getElementById('trend-chart');
      if (!chartDom) return;

      this.trendChart = echarts.init(chartDom);

      const years = this.trendData.map(item => item.year);
      const avgLeftData = this.trendData.map(item => parseFloat(item.avgLeft).toFixed(2));
      const avgRightData = this.trendData.map(item => parseFloat(item.avgRight).toFixed(2));
      const normalRateData = this.trendData.map(item => parseFloat((item.normalRate * 100).toFixed(2)));
      const myopiaRateData = this.trendData.map(item => parseFloat((item.myopiaRate * 100).toFixed(2)));

      const option = {
        title: {
          text: '年度视力趋势对比',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        legend: {
          data: ['左眼平均视力', '右眼平均视力', '视力正常率', '近视率'],
          bottom: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            data: years
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: '视力值',
            position: 'left',
            axisLabel: {
              formatter: '{value}'
            }
          },
          {
            type: 'value',
            name: '比率',
            position: 'right',
            axisLabel: {
              formatter: '{value}%'
            }
          }
        ],
        series: [
          {
            name: '左眼平均视力',
            type: 'line',
            yAxisIndex: 0,
            data: avgLeftData,
            symbol: 'circle',
            symbolSize: 8
          },
          {
            name: '右眼平均视力',
            type: 'line',
            yAxisIndex: 0,
            data: avgRightData,
            symbol: 'circle',
            symbolSize: 8
          },
          {
            name: '视力正常率',
            type: 'line',
            yAxisIndex: 1,
            data: normalRateData,
            symbol: 'circle',
            symbolSize: 8
          },
          {
            name: '近视率',
            type: 'line',
            yAxisIndex: 1,
            data: myopiaRateData,
            symbol: 'circle',
            symbolSize: 8
          }
        ]
      };

      this.trendChart.setOption(option);
    },
    // 初始化性别对比图表
    initGenderChart() {
      if (!this.genderData || this.genderData.length === 0) return;

      const chartDom = document.getElementById('gender-chart');
      if (!chartDom) return;

      this.genderChart = echarts.init(chartDom);

      const genderLabels = this.genderData.map(item => item.gender === 'M' ? '男' : item.gender === 'F' ? '女' : item.gender);
      const normalRateData = this.genderData.map(item => parseFloat((item.normalRate * 100).toFixed(2)));
      const mildRateData = this.genderData.map(item => parseFloat((item.mildRate * 100).toFixed(2)));
      const moderateRateData = this.genderData.map(item => parseFloat((item.moderateRate * 100).toFixed(2)));
      const highRateData = this.genderData.map(item => parseFloat((item.highRate * 100).toFixed(2)));

      const option = {
        title: {
          text: `${this.queryParams.year}年性别视力对比`,
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            let result = params[0].name + '<br/>';
            params.forEach(item => {
              result += item.seriesName + ': ' + item.value + '%<br/>';
            });
            return result;
          }
        },
        legend: {
          data: ['正常', '轻度', '中度', '重度'],
          bottom: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: genderLabels
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value}%'
          }
        },
        series: [
          {
            name: '正常',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: normalRateData,
            itemStyle: {
              color: '#67C23A'
            }
          },
          {
            name: '轻度',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: mildRateData,
            itemStyle: {
              color: '#E6A23C'
            }
          },
          {
            name: '中度',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: moderateRateData,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '重度',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: highRateData,
            itemStyle: {
              color: '#F56C6C'
            }
          }
        ]
      };

      this.genderChart.setOption(option);
    },
    // 初始化年龄段对比图表
    initAgeChart() {
      if (!this.ageGroupData || this.ageGroupData.length === 0) return;

      const chartDom = document.getElementById('age-chart');
      if (!chartDom) return;

      this.ageChart = echarts.init(chartDom);

      const ageLabels = this.ageGroupData.map(item => item.ageGroup);
      const normalRateData = this.ageGroupData.map(item => parseFloat((item.normalRate * 100).toFixed(2)));
      const mildRateData = this.ageGroupData.map(item => parseFloat((item.mildRate * 100).toFixed(2)));
      const moderateRateData = this.ageGroupData.map(item => parseFloat((item.moderateRate * 100).toFixed(2)));
      const highRateData = this.ageGroupData.map(item => parseFloat((item.highRate * 100).toFixed(2)));

      const option = {
        title: {
          text: `${this.queryParams.year}年年龄段视力对比`,
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            let result = params[0].name + '<br/>';
            params.forEach(item => {
              result += item.seriesName + ': ' + item.value + '%<br/>';
            });
            return result;
          }
        },
        legend: {
          data: ['正常', '轻度', '中度', '重度'],
          bottom: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ageLabels
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value}%'
          }
        },
        series: [
          {
            name: '正常',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: normalRateData,
            itemStyle: {
              color: '#67C23A'
            }
          },
          {
            name: '轻度',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: mildRateData,
            itemStyle: {
              color: '#E6A23C'
            }
          },
          {
            name: '中度',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: moderateRateData,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '重度',
            type: 'bar',
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: highRateData,
            itemStyle: {
              color: '#F56C6C'
            }
          }
        ]
      };

      this.ageChart.setOption(option);
    },
    // 处理年份变化
    handleYearChange() {
      this.getStatData();
    },
    // 处理图表类型变化
    handleChartTypeChange() {
      this.initDistributionChart();
    },
    // 处理设置趋势年份范围
    handleSetTrendYears() {
      this.yearRangeDialogVisible = true;
    },
    // 确认趋势年份范围
    confirmTrendYears() {
      if (this.trendYearRange.startYear > this.trendYearRange.endYear) {
        this.$message.error('开始年份不能大于结束年份');
        return;
      }
      this.yearRangeDialogVisible = false;
      this.getTrendStat();
    },
    // 格式化比率
    formatRate(rate) {
      if (rate === undefined || rate === null) return '0%';
      return (rate * 100).toFixed(2) + '%';
    },
    // 格式化AI报告（将Markdown格式转换为HTML）
    formatAiReport(report) {
      if (!report) return '';

      // 将Markdown标题转换为HTML标题
      let formattedReport = report
        // 处理一级标题 # 标题
        .replace(/^#\s+(.+)$/gm, '<h1 style="color: #303133; font-size: 24px; font-weight: bold; margin: 20px 0 15px 0; border-bottom: 2px solid #409EFF; padding-bottom: 8px;">$1</h1>')
        // 处理二级标题 ## 标题
        .replace(/^##\s+(.+)$/gm, '<h2 style="color: #409EFF; font-size: 20px; font-weight: bold; margin: 18px 0 12px 0; border-left: 4px solid #409EFF; padding-left: 12px;">$1</h2>')
        // 处理三级标题 ### 标题
        .replace(/^###\s+(.+)$/gm, '<h3 style="color: #67C23A; font-size: 18px; font-weight: bold; margin: 15px 0 10px 0;">$1</h3>')
        // 处理四级标题 #### 标题
        .replace(/^####\s+(.+)$/gm, '<h4 style="color: #E6A23C; font-size: 16px; font-weight: bold; margin: 12px 0 8px 0;">$1</h4>')
        // 处理粗体文本 **文本**
        .replace(/\*\*(.+?)\*\*/g, '<strong style="font-weight: bold; color: #303133;">$1</strong>')
        // 处理斜体文本 *文本*
        .replace(/\*(.+?)\*/g, '<em style="font-style: italic; color: #606266;">$1</em>')
        // 处理列表项 - 项目
        .replace(/^-\s+(.+)$/gm, '<li style="margin: 5px 0; padding-left: 20px; position: relative;">$1</li>')
        // 处理数字列表项 1. 项目
        .replace(/^\d+\.\s+(.+)$/gm, '<li style="margin: 5px 0; padding-left: 20px; position: relative;">$1</li>')
        // 将连续的列表项包装在ul标签中
        .replace(/(<li[^>]*>.*<\/li>)/gs, '<ul style="margin: 10px 0; padding-left: 20px;">$1</ul>')
        // 处理段落（将连续的非标题、非列表行包装在p标签中）
        .replace(/^(?!<[hou][1-6l]|<li|<ul)(.+)$/gm, '<p style="margin: 8px 0; line-height: 1.6; color: #606266;">$1</p>')
        // 最后将换行符转换为空格（因为已经用p标签处理了段落）
        .replace(/\n/g, ' ');

      return formattedReport;
    },
    // 调整图表大小
    resizeCharts() {
      this.distributionChart && this.distributionChart.resize();
      this.trendChart && this.trendChart.resize();
      this.genderChart && this.genderChart.resize();
      this.ageChart && this.ageChart.resize();
    }
  }
};
</script>

<style scoped>
.stat-card {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 20px;
  text-align: center;
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.stat-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
.chart-container {
  margin-top: 20px;
}
.ai-report {
  padding: 30px;
  min-height: 200px;
  background-color: #ffffff;
  border-radius: 8px;
  line-height: 1.8;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.ai-report h1 {
  color: #303133 !important;
  font-size: 24px !important;
  font-weight: bold !important;
  margin: 20px 0 15px 0 !important;
  border-bottom: 2px solid #409EFF !important;
  padding-bottom: 8px !important;
}

.ai-report h2 {
  color: #409EFF !important;
  font-size: 20px !important;
  font-weight: bold !important;
  margin: 18px 0 12px 0 !important;
  border-left: 4px solid #409EFF !important;
  padding-left: 12px !important;
}

.ai-report h3 {
  color: #67C23A !important;
  font-size: 18px !important;
  font-weight: bold !important;
  margin: 15px 0 10px 0 !important;
}

.ai-report h4 {
  color: #E6A23C !important;
  font-size: 16px !important;
  font-weight: bold !important;
  margin: 12px 0 8px 0 !important;
}

.ai-report p {
  margin: 8px 0 !important;
  line-height: 1.6 !important;
  color: #606266 !important;
}

.ai-report ul {
  margin: 10px 0 !important;
  padding-left: 20px !important;
}

.ai-report li {
  margin: 5px 0 !important;
  padding-left: 20px !important;
  position: relative !important;
}

.ai-report strong {
  font-weight: bold !important;
  color: #303133 !important;
}

.ai-report em {
  font-style: italic !important;
  color: #606266 !important;
}
.empty-report {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}
.export-btns {
  text-align: center;
  padding: 20px 0;
}
</style>
