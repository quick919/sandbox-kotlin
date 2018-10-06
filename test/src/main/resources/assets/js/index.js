import SelectablePublisher from "./modules/selectable-publisher.js";
import Modal from "./modules/modal.js";
import Hub from "./modules/vue-hub.js";
import ContentSelector from "./modules/content-selector.js";

new Vue({
  el: "#app2",
  mixins: [SelectablePublisher, ContentSelector],
  data() {
    return {
      contents: [],
      selected: "all",
      searchTitle: "",
      errored: false,
      uploadFile: null,
      readingState: 0
    };
  },
  components: {
    Modal
  },
  mounted() {
    this.$nextTick(
      function () {
        Hub.$on("updateContent", this.updateContent);
        Hub.$on("deleteContent", this.deleteContent);
      }.bind(this)
    );
  },
  created() {
    this.$nextTick(function () {
      this.getData();
    });
  },
  methods: {
    getData: function () {
      const self = this;
      this._get(self, "contents", {
        publisher: self.selected,
        readingState: self.readingState
      });
    },
    openModal: function (content) {
      Hub.$emit("open-modal", content);
    },
    closeModal: function () {
      Hub.$emit("close-modal");
    },
    updateContent: function (content) {
      this.contents.forEach((elm, index) => {
        if (elm.id !== content.id) {
          return;
        }
        this.contents.splice(index, 1, content);
      });
    },
    deleteContent: function (content) {
      this.contents.forEach((elm, index) => {
        if (elm.id !== content.id) {
          return;
        }
        this.contents.splice(index, 1);
      });
    },
    changePublisher: function () {
      this.getData();
    },
    search: function () {
      const self = this;
      this._get(self, "/search", {
        searchTitle: self.searchTitle
      });
    },
    output: function () {
      axios
        .get("/output", {
          params: {
            searchTitle: this.searchTitle,
            publisher: this.selected,
            readingState: this.readingState
          }
        })
        .then(function (response) {
          var json = JSON.stringify(response.data);
          const url = window.URL.createObjectURL(new Blob([json]));
          const link = document.createElement("a");
          link.href = url;
          link.setAttribute("download", "test.json");
          document.body.appendChild(link);
          link.click();
        })
        .catch(
          function (error) {
            console.log(error);
            self.errored = true;
          }.bind(this)
        );
    },
    selectedFile: function (e) {
      e.preventDefault();
      const file = e.target.files[0];
      if (!(file.type === "application/json")) {
        alert("Please specify json file.");
        return false;
      }
      this.uploadFile = file;
    },
    upload: function () {
      let formData = new FormData();
      if (this.uploadFile === null) {
        return;
      }
      formData.append("file", this.uploadFile);
      let config = {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      };
      axios
        .post("/upload", formData, config)
        .then(function (response) {
          document.location = "/";
        })
        .catch(function (error) {});
    },
    done: function () {
      axios.post("/done",
        JSON.stringify(this.multiSelectedContents)
      ).then(function (response) {
        document.location = "/";
      }).catch(function (response) {
        console.log(response);
      });
    },
    undone: function () {
      axios.post("/undone",
        JSON.stringify(this.multiSelectedContents)
      ).then(function (response) {
        document.location = "/";
      }).catch(function (response) {
        console.log(response);
      });
    },
    _get: function (self, url, params) {
      axios
        .get(url, {
          params: params
        })
        .then(function (response) {
          self.contents = response.data;
        })
        .catch(function (error) {
          console.log(error);
          self.errored = true;
        });
    }
  }
});