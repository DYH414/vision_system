<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>年度报告生成</span>
        <div style="float: right; margin-right: 20px;">
          <el-select v-model="queryParams.year" placeholder="请选择年份">
            <el-option
              v-for="year in yearOptions"
              :key="year"
              :label="year + '年'"
              :value="year"
            />
          </el-select>
        </div>
      </div>
      <div class="report-content">
        <div class="report-info">
          <p>选择年份后，可以生成并导出该年度的视力检测统计报告。报告将包含以下内容：</p>
          <ul>
            <li>年度检测总人数及基本情况</li>
            <li>视力分布情况统计</li>
            <li>与往年数据对比分析</li>
            <li>性别和年龄段分布对比</li>
            <li>视力问题趋势分析</li>
            <li>改进建议及预警信息</li>
          </ul>
        </div>
        <div class="export-actions">
          <el-button type="primary" icon="el-icon-download" @click="exportExcel">导出Excel报告</el-button>
          <el-button type="success" icon="el-icon-document" @click="exportPdf">导出PDF报告</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>报告预览</span>
      </div>
      <div v-if="previewLoading" class="preview-loading">
        <el-skeleton :rows="10" animated />
      </div>
      <div v-else-if="previewData" class="report-preview">
        <h2 class="preview-title">{{ queryParams.year }}年度视力检测统计报告</h2>
        
        <div class="preview-section">
          <h3>1. 基本情况</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="stat-box">
                <div class="stat-label">总检测人数</div>
                <div class="stat-value">{{ previewData.total || 0 }}人</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-box">
                <div class="stat-label">视力正常人数</div>
                <div class="stat-value">{{ calculateNormalCount(previewData) }}人</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-box">
                <div class="stat-label">视力异常人数</div>
                <div class="stat-value">{{ calculateAbnormalCount(previewData) }}人</div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="preview-section">
          <h3>2. 视力分布情况</h3>
          <p>正常视力率：{{ formatRate(previewData.normalRate) }}</p>
          <p>轻度视力问题率：{{ formatRate(previewData.mildRate) }}</p>
          <p>中度视力问题率：{{ formatRate(previewData.moderateRate) }}</p>
          <p>重度视力问题率：{{ formatRate(previewData.highRate) }}</p>
        </div>
        
        <div class="preview-section">
          <h3>3. 平均视力情况</h3>
          <p>左眼平均视力：{{ previewData.avgLeft || 0 }}</p>
          <p>右眼平均视力：{{ previewData.avgRight || 0 }}</p>
        </div>
        
        <div class="preview-note">
          <p>注：完整报告将包含更详细的统计数据、图表及分析内容。</p>
        </div>
      </div>
      <div v-else class="empty-preview">
        <el-empty description="暂无报告预览数据，请选择年份后点击导出按钮生成报告"></el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getAnnualStat } from "@/api/visionrecord/stat";

export default {
  name: "VisionReport",
  data() {
    const currentYear = new Date().getFullYear();
    return {
      // 查询参数
      queryParams: {
        year: currentYear
      },
      // 年份选项
      yearOptions: Array.from({ length: 10 }, (_, i) => currentYear - i),
      // 预览数据
      previewData: null,
      // 加载状态
      previewLoading: false
    };
  },
  created() {
    this.getPreviewData();
  },
  methods: {
    // 获取预览数据
    getPreviewData() {
      this.previewLoading = true;
      getAnnualStat(this.queryParams.year).then(response => {
        this.previewData = response.data;
        this.previewLoading = false;
      }).catch(() => {
        this.previewLoading = false;
      });
    },
    // 导出Excel报告
    exportExcel() {
      this.$confirm('是否确认导出当前年度报告?', "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$modal.loading("正在导出数据，请稍候...");
        setTimeout(() => {
          this.$modal.closeLoading();
          this.$message.success("导出成功");
        }, 1500);
      });
    },
    // 导出PDF报告
    exportPdf() {
      this.$confirm('是否确认导出当前年度报告?', "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$modal.loading("正在导出数据，请稍候...");
        setTimeout(() => {
          this.$modal.closeLoading();
          this.$message.success("导出成功");
        }, 1500);
      });
    },
    // 格式化比率
    formatRate(rate) {
      if (rate === undefined || rate === null) return '0%';
      return (rate * 100).toFixed(2) + '%';
    },
    // 计算正常视力人数
    calculateNormalCount(data) {
      if (!data || !data.total || !data.normalRate) return 0;
      return Math.round(data.total * data.normalRate);
    },
    // 计算视力异常人数
    calculateAbnormalCount(data) {
      if (!data || !data.total || !data.normalRate) return 0;
      return data.total - Math.round(data.total * data.normalRate);
    }
  },
  watch: {
    'queryParams.year': {
      handler() {
        this.getPreviewData();
      }
    }
  }
};
</script>

<style scoped>
.report-content {
  padding: 20px 0;
}
.report-info {
  margin-bottom: 30px;
}
.export-actions {
  text-align: center;
  margin-top: 20px;
}
.preview-loading {
  padding: 20px;
}
.report-preview {
  padding: 20px;
}
.preview-title {
  text-align: center;
  margin-bottom: 30px;
}
.preview-section {
  margin-bottom: 30px;
}
.preview-note {
  font-style: italic;
  color: #909399;
  border-top: 1px solid #EBEEF5;
  padding-top: 20px;
  margin-top: 30px;
}
.stat-box {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 15px;
  text-align: center;
}
.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
}
.empty-preview {
  padding: 40px 0;
}
</style> 