export default {
  data() {
    return {
      selectedContent: {},
      selectedContentIndex: -1
    }
  },
  methods: {
    selectThumbnail: function (content, index) {
      if (this.selectedContentIndex === index) {
        this.selectedContent = {};
        this.selectedContentIndex = -1;
      } else {
        this.selectedContentIndex = index;
        this.selectedContent = content;
      }
    }
  }
}