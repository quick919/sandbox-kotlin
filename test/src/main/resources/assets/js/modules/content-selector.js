export default {
  data() {
    return {
      selectedContent: {},
      selectedContentIndex: -1,
      multiSelectedContent: []
    }
  },
  methods: {
    selectContent: function (content, index) {
      if (this.selectedContentIndex === index) {
        this.selectedContent = {};
        this.selectedContentIndex = -1;
      } else {
        this.selectedContentIndex = index;
        this.selectedContent = content;
      }
    },
    unselectContent: function () {
      this.selectedContent = {};
      this.selectedContentIndex = -1;
    },
    multiSelectContent: function (content, index) {
      console.log(this.multiSelectedContent)
      if (this.multiSelectedContent.indexOf(index) == -1) {
        this.multiSelectedContent.push(index);
      } else {
        this.multiSelectedContent.forEach((element, idx) => {
          if (element === index) {
            this.multiSelectedContent.splice(idx, 1);
          }
        });
      }
    }
  }
}