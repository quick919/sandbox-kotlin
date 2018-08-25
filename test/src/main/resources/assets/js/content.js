import SelectablePublisher from './modules/selectable-publisher.js'
import Modal2 from './modules/modal.js'
import Hub from './modules/vue-hub.js'
import ContentSelector from './modules/content-selector.js'

new Vue({
  el: "#app",
  mixins: [SelectablePublisher, ContentSelector],
  data() {
    return {
      publisher: "Amazon",
      isActive: false,
      thumbnails: [],
      activeNumber: "",
      content: {},
      contentName: "",
      selectedContent: {},
      selectedContentIndex: -1
    };
  },
  components: {
    Modal2
  },
  methods: {
    post: function () {
      this.selectedContent.publisher = this.publisher;
      axios
        .post("/content", {
          content: this.selectedContent
        })
        .then(function (response) {
          console.log(response);
          location = "/";
        })
        .catch(function (error) {
          console.log(error);
        });
    },
    fetchThumbnail: function () {
      const self = this;
      axios
        .get("/thumbnail", {
          params: {
            name: this.contentName
          }
        })
        .then(function (response) {
          console.log(response.data);
          console.log(self.thumbnails);
          self.thumbnails = response.data;
          console.log(self.thumbnails);
        })
        .catch(function (error) {
          console.log(error);
        });
    },
    openModal: function () {
      // var content = {
      //   id: "",
      //   title: "",
      //   imageLink: "",
      //   isbnCode: "",
      //   publisher: ""
      // }
      console.log("openModal")
      Hub.$emit("open-modal", {});
    }
  }
});