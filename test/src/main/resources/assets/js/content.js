import SelectablePublisher from './modules/selectable-publisher.js'
import Modal2 from './modules/modal.js'
import Hub from './modules/vue-hub.js'

new Vue({
  el: "#app",
  mixins: [SelectablePublisher],
  data() {
    return {
      publisher: "Amazon",
      isActive: false,
      thumbnails: [],
      activeNumber: "",
      content: {},
      contentName: ""
    };
  },
  components: {
    Modal2
  },
  methods: {
    post: function () {
      this.content.publisher = this.publisher;
      axios
        .post("/content", {
          content: this.content
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
    selectThumbnail: function (thumbnail, index) {
      const self = this;
      if (self.activeNumber === "") {
        var obj = self.thumbnails[index];
        obj.isActive = true;
        self.activeNumber = index;
        self.thumbnails.splice(index, 1, obj);
        self.content = thumbnail;
      } else {
        var obj = self.thumbnails[self.activeNumber];
        obj.isActive = false;
        self.thumbnails.splice(self.activeNumber, 1, obj);
        if (self.activeNumber === index) {
          self.activeNumber = "";
        } else {
          var obj = self.thumbnails[index];
          obj.isActive = true;
          self.activeNumber = index;
          self.thumbnails.splice(index, 1, obj);
          self.content = thumbnail;
        }
      }
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