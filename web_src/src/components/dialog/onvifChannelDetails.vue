<template>
  <div id="onvifChannelDetails">
    <el-dialog
      title="Onvif通道详情"
      width="=80%"
      top="2rem"
      :close-on-click-modal="false"
      :visible.sync="showDialog"
      :destroy-on-close="true"
      @close="close()"
    >
      <div id="shared" style="margin-top: 1rem;margin-right: 100px;">
        <el-descriptions :column="2" :span="2">
          <el-descriptions-item label="名称" >{{channelData.name}}</el-descriptions-item>
          <el-descriptions-item label="IP">{{channelData.ip}}</el-descriptions-item>
          <el-descriptions-item label="端口">{{channelData.port}}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{channelData.username}}</el-descriptions-item>
          <el-descriptions-item label="密码">
            <el-tag size="small">{{channelData.password}}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="国标编号" >{{channelData.gbId}}</el-descriptions-item>
          <el-descriptions-item label="制造商" >{{channelData.manufacturer}}</el-descriptions-item>
          <el-descriptions-item label="型号" >{{channelData.model}}</el-descriptions-item>
          <el-descriptions-item label="固件版本" >{{channelData.firmwareVersion}}</el-descriptions-item>
          <el-descriptions-item label="序列号" >{{channelData.serialNumber}}</el-descriptions-item>
          <el-descriptions-item label="硬件Id" >{{channelData.hardwareId}}</el-descriptions-item>
          <el-descriptions-item label="设备时间" >{{channelData.date}}</el-descriptions-item>

          <el-descriptions-item label="经度" >{{channelData.longitude}}</el-descriptions-item>
          <el-descriptions-item label="纬度" >{{channelData.latitude}}</el-descriptions-item>
          <el-descriptions-item label="创建时间" >{{channelData.createTime}}</el-descriptions-item>
          <el-descriptions-item label="更新时间" >{{channelData.updateTime}}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions :column="1" :span="2">
          <el-descriptions-item label="TCP实时流" >
            <el-tag size="small">{{channelData.liveStreamTcp}}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="UDP实时流" >
            <el-tag size="small">{{channelData.liveStreamUdp}}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="多播实时流" >
            <el-tag size="small">{{channelData.liveStreamMulticast}}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="回    放    流" >
<!--            <el-tag size="small">{{channelData.replayStream}}</el-tag>-->
            <el-tag size="small">暂不支持</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "configInfo",
  props: {},
  computed: {},
  created() {},
  data() {
    return {
      showDialog: false,
      channelData: {
      }
    };
  },
  methods: {
    openDialog: function (data) {
      console.log(data)
      this.showDialog = true;
      this.channelData = data;
    },
    close: function () {
      this.showDialog = false;
    },
  },
};
</script>
