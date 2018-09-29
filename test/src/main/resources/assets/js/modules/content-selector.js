export default {
  data() {
    return {
      selectedContent: {},
      selectedContentIndex: -1,
      multiSelectedContentsIndex: []
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
      console.log(this.multiSelectedContentsIndex)
      if (this.multiSelectedContentsIndex.indexOf(index) == -1) {
        this.multiSelectedContentsIndex.push(index);
      } else {
        this.multiSelectedContentsIndex.forEach((element, idx) => {
          if (element === index) {
            this.multiSelectedContentsIndex.splice(idx, 1);
          }
        });
      }
    }
  }
}