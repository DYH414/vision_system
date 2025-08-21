<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="人员" prop="personId">
        <el-select v-model="queryParams.personId" placeholder="请选择人员" clearable filterable>
          <el-option
            v-for="item in personOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="检测日期" prop="checkDate">
        <el-date-picker clearable
          v-model="queryParams.checkDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择检测日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="年度" prop="year">
        <el-input
          v-model="queryParams.year"
          placeholder="请输入年度"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="诊断" prop="diagnosis">
        <el-input
          v-model="queryParams.diagnosis"
          placeholder="请输入诊断"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker clearable
          v-model="queryParams.createTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="更新时间" prop="updateTime">
        <el-date-picker clearable
          v-model="queryParams.updateTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择更新时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['visionrecord:visonrecord:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['visionrecord:visonrecord:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['visionrecord:visonrecord:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['visionrecord:visonrecord:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="visonrecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录ID" align="center" prop="id" />
      <el-table-column label="人员姓名" align="center" prop="personName" :formatter="personNameFormat" />
      <el-table-column label="检测日期" align="center" prop="checkDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.checkDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="年度" align="center" prop="year" />
      <el-table-column label="左眼视力" align="center" prop="leftEye" />
      <el-table-column label="右眼视力" align="center" prop="rightEye" />
      <el-table-column label="诊断" align="center" prop="diagnosis">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.VISION_LEVEL" :value="scope.row.diagnosis"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['visionrecord:visonrecord:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['visionrecord:visonrecord:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改视力监测记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="人员" prop="personId">
          <el-select v-model="form.personId" placeholder="请选择人员" filterable>
            <el-option
              v-for="item in personOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="检测日期" prop="checkDate">
          <el-date-picker clearable
            v-model="form.checkDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择检测日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="年度" prop="year">
          <el-input v-model="form.year" placeholder="请输入年度" />
        </el-form-item>
        <el-form-item label="左眼视力" prop="leftEye">
          <el-input v-model="form.leftEye" placeholder="请输入左眼视力" />
        </el-form-item>
        <el-form-item label="右眼视力" prop="rightEye">
          <el-input v-model="form.rightEye" placeholder="请输入右眼视力" />
        </el-form-item>
        <el-form-item label="诊断" prop="diagnosis">
          <el-select v-model="form.diagnosis" placeholder="请选择诊断">
            <el-option
              v-for="dict in dict.type.VISION_LEVEL"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listVisonrecord, getVisonrecord, delVisonrecord, addVisonrecord, updateVisonrecord } from "@/api/visionrecord/visonrecord"
import { listPerson, getPerson } from "@/api/visionrecord/person"

export default {
  name: "Visonrecord",
  dicts: ['VISION_LEVEL'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 视力监测记录表格数据
      visonrecordList: [],
      // 人员列表数据
      personOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        personId: null,
        checkDate: null,
        year: null,
        diagnosis: null,
        createTime: null,
        updateTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        personId: [
          { required: true, message: "人员ID不能为空", trigger: "blur" }
        ],
        checkDate: [
          { required: true, message: "检测日期不能为空", trigger: "blur" }
        ],
        year: [
          { required: true, message: "年度不能为空", trigger: "blur" }
        ],
        leftEye: [
          { required: true, message: "左眼视力不能为空", trigger: "blur" }
        ],
        rightEye: [
          { required: true, message: "右眼视力不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
    this.getPersonList()
  },
  methods: {
    /** 查询视力监测记录列表 */
    getList() {
      this.loading = true
      listVisonrecord(this.queryParams).then(response => {
        this.visonrecordList = response.rows
        this.total = response.total
        this.loading = false
        // 获取每条记录对应的人员信息
        this.getPersonNames()
      })
    },
    /** 获取人员列表 */
    getPersonList() {
      listPerson().then(response => {
        this.personOptions = response.rows
      })
    },
    /** 获取人员姓名 */
    getPersonNames() {
      // 获取所有记录中的人员ID
      const personIds = this.visonrecordList.map(item => item.personId)
      // 去重
      const uniqueIds = [...new Set(personIds)]
      // 为每个记录添加personName属性
      uniqueIds.forEach(id => {
        if (id) {
          getPerson(id).then(res => {
            const personName = res.data.name
            // 更新所有相同ID的记录
            this.visonrecordList.forEach(item => {
              if (item.personId === id) {
                this.$set(item, 'personName', personName)
              }
            })
          })
        }
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        personId: null,
        checkDate: null,
        year: null,
        leftEye: null,
        rightEye: null,
        diagnosis: null,
        remark: null,
        createTime: null,
        updateTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      // 确保已加载人员列表
      if (this.personOptions.length === 0) {
        this.getPersonList()
      }
      this.open = true
      this.title = "添加视力监测记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getVisonrecord(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改视力监测记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateVisonrecord(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addVisonrecord(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除视力监测记录编号为"' + ids + '"的数据项？').then(function() {
        return delVisonrecord(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('visionrecord/visonrecord/export', {
        ...this.queryParams
      }, `visonrecord_${new Date().getTime()}.xlsx`)
    },
    /** 人员姓名格式化 */
    personNameFormat(row, column, cellValue) {
      return cellValue || '--'
    }
  }
}
</script>
