<template>
  <div id="channelList" style="width: 100%">
    <div class="page-header">
      <div class="page-title">
        <el-button icon="el-icon-back" size="mini" style="font-size: 20px; color: #000;" type="text" @click="showDevice" ></el-button>
        <el-divider direction="vertical"></el-divider>
        Onvif通道列表
      </div>
      <div class="page-header-btn">
        <el-button size="mini" @click="discovery()" style="margin-right: 3rem">搜索设备</el-button>
      <el-button icon="el-icon-refresh-right" circle size="mini" @click="refresh()"></el-button>
    </div>
  </div>
  <devicePlayer ref="devicePlayer" ></devicePlayer>
  <onvifChannelEdit ref="onvifChannelEdit" ></onvifChannelEdit>
  <onvifChannelDetails ref="onvifChannelDetails" ></onvifChannelDetails>
  <el-container v-loading="isLoging" style="height: 82vh;">
    <el-main style="padding: 5px;">
      <el-table ref="channelListTable" :data="onvifDeviceChannelList" :height="winHeight" style="width: 100%" header-row-class-name="table-header">
        <el-table-column prop="name" label="名称" min-width="200">
        </el-table-column>
<!--        <el-table-column label="快照" min-width="120">-->
<!--          <template v-slot:default="scope">-->
<!--            <el-image-->
<!--              :src="getSnap(scope.row)"-->
<!--              :preview-src-list="getBigSnap(scope.row)"-->
<!--              @error="getSnapErrorEvent(scope.row.deviceId, scope.row.channelId)"-->
<!--              :fit="'contain'"-->
<!--              style="width: 60px">-->
<!--              <div slot="error" class="image-slot">-->
<!--                <i class="el-icon-picture-outline"></i>-->
<!--              </div>-->
<!--            </el-image>-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column prop="manufacturer" label="厂家" min-width="120">
        </el-table-column>
        <el-table-column prop="gbId" label="国标ID" min-width="120">
        </el-table-column>
        <el-table-column label="地址信息"  min-width="200">
          <template slot-scope="scope">
            <span>{{ scope.row.ip }}:{{ scope.row.port }}</span>
          </template>
        </el-table-column>
        <el-table-column label="位置信息"  min-width="200">
          <template slot-scope="scope">
            <span v-if="scope.row.longitude*scope.row.latitude > 0">{{ scope.row.longitude }},<br>{{ scope.row.latitude }}</span>
            <span v-if="scope.row.longitude*scope.row.latitude === 0">无</span>
          </template>
        </el-table-column>
<!--        <el-table-column label="开启音频" min-width="120">-->
<!--          <template slot-scope="scope">-->
<!--            <el-switch @change="updateChannel(scope.row)" v-model="scope.row.hasAudio" active-color="#409EFF">-->
<!--            </el-switch>-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column label="状态" min-width="120">
          <template slot-scope="scope">
            <div slot="reference" class="name-wrapper">
              <el-tag size="medium" >{{scope.row.status?"在线":"离线"}}</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" min-width="280" fixed="right">
          <template slot-scope="scope">
            <el-button size="medium" v-if="scope.row.liveStreamTcp != null || scope.row.liveStreamUdp != null" icon="el-icon-video-play" type="text" @click="play(scope.row)">播放</el-button>

            <el-button size="medium" v-if="scope.row.app != null && scope.row.stream != null" icon="el-icon-switch-button" type="text"  style="color: #f56c6c"
                       @click="stop(scope.row)">停止
            </el-button>
            <el-button size="medium" type="text" icon="el-icon-edit" @click="edit(scope.row)">编辑</el-button>
            <el-button size="medium" type="text" icon="el-icon-more" @click="details(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        style="float: right"
        @size-change="handleSizeChange"
        @current-change="currentChange"
        :current-page="currentPage"
        :page-size="count"
        :page-sizes="[15, 25, 35, 50]"
        layout="total, sizes, prev, pager, next"
        :total="total">
      </el-pagination>
    </el-main>
  </el-container>

  <!--设备列表-->

  </div>
</template>

<script>
import devicePlayer from './dialog/devicePlayer.vue'
import uiHeader from '../layout/UiHeader.vue'
import onvifChannelEdit from './dialog/onvifChannelEdit.vue'
import onvifChannelDetails from './dialog/onvifChannelDetails.vue'

export default {
  name: 'channelList',
  components: {
    devicePlayer,
    uiHeader,
    onvifChannelEdit,
    onvifChannelDetails,
  },
  data() {
    return {
      onvifDeviceId: this.$route.params.onvifDeviceId,
      onvifDeviceChannelList: [],
      updateLooper: 0, //数据刷新轮训标志
      searchSrt: "",
      channelType: "",
      online: "",
      winHeight: window.innerHeight - 200,
      currentPage: 1,
      count: 15,
      total: 0,
      beforeUrl: "/onvifDeviceList",
      isLoging: false,
      showTree: false,
      loadSnap: {}
    };
  },

  mounted() {
    this.initData();

  },
  destroyed() {
    this.$destroy('videojs');
    clearTimeout(this.updateLooper);
  },
  methods: {
    discovery: function (){
      let that = this;
      this.$confirm("扫描设备将清除现有通道", '提示', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        center: true,
        type: 'warning'
      }).then(() => {
        if (typeof (this.onvifDeviceId) == "undefined") return;
        this.$axios({
          method: 'get',
          url: `/api/onvif/device/discovery`,
          params: {
            deviceId: that.onvifDeviceId,
          }
        }).then(function (res) {
          if (res.data.code !== 0) {
            that.$message.error(res.data.msg);
          }
        }).catch(function (error) {
          console.log(error);
        });
      }).catch(() => {
        console.log(error);
      });
    },
    initData: function () {
      this.getOnvifDeviceChannelList();
    },
    initParam: function () {
      this.deviceId = this.$route.params.onvifDeviceId;
      this.currentPage = 1;
      this.count = 15;
    },
    currentChange: function (val) {
      this.currentPage = val;
      this.initData();
    },
    handleSizeChange: function (val) {
      this.count = val;
      this.getOnvifDeviceChannelList();
    },
    getOnvifDeviceChannelList: function () {
      let that = this;
      if (typeof (this.$route.params.onvifDeviceId) == "undefined") return;
      this.$axios({
        method: 'get',
        url: `/api/onvif/channel/all`,
        params: {
          page: that.currentPage,
          count: that.count,
          deviceId: that.onvifDeviceId,
        }
      }).then(function (res) {
        if (res.data.code === 0) {
          that.total = res.data.data.total;
          that.onvifDeviceChannelList = res.data.data.list;
          // 防止出现表格错位
          that.$nextTick(() => {
            that.$refs.channelListTable.doLayout();
          })
        }

      }).catch(function (error) {
        console.log(error);
      });
    },

    //通知设备上传媒体流
    play: function (itemData) {

    },

    //通知设备上传媒体流
    edit: function (row) {

      this.$refs.onvifChannelEdit.openDialog(row, ()=>{
        this.$refs.onvifChannelEdit.close();
        this.initData();
      })
    },

    //通知设备上传媒体流
    stop: function (row) {

    },

    details: function (row) {
      this.$refs.onvifChannelDetails.openDialog(row)
    },
    showDevice: function () {
      this.$router.push(this.beforeUrl);
    },
    refresh: function () {
      this.initData();
    },

  }
};
</script>

<style>
.videoList {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
}

.video-item {
  position: relative;
  width: 15rem;
  height: 10rem;
  margin-right: 1rem;
  background-color: #000000;
}

.video-item-img {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  width: 100%;
  height: 100%;
}

.video-item-img:after {
  content: "";
  display: inline-block;
  position: absolute;
  z-index: 2;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  width: 3rem;
  height: 3rem;
  background-image: url("../assets/loading.png");
  background-size: cover;
  background-color: #000000;
}

.video-item-title {
  position: absolute;
  bottom: 0;
  color: #000000;
  background-color: #ffffff;
  line-height: 1.5rem;
  padding: 0.3rem;
  width: 14.4rem;
}
</style>
