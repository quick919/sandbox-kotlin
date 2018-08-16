import SelectablePublisher from './modules/selectable-publisher.js'

const Hub = new Vue();

new Vue({
  el: "#app2",
  mixins: [SelectablePublisher],
  data() {
    return {
      contents: [],
      selected: "all",
      searchTitle: "",
      errored: false
    };
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
        publisher: self.selected
      })
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
      })
    },
    _get: function (self, url, params) {
      axios.get(url, {
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

Vue.component("modal", {
  template: "#modal-template",
  mixins: [SelectablePublisher],
  data: function () {
    return {
      active: false,
      editContent: {}
    };
  },
  methods: {
    open: function (content) {
      this.active = true;
      this.editContent = {
        id: content.id,
        title: content.title,
        imageLink: content.imageLink,
        isbnCode: content.isbnCode,
        publisher: content.publisher
      };
    },
    close: function () {
      this.active = false;
    },
    update: function (content) {
      var self = this;
      this._post("/edit", content, function () {
        Hub.$emit("updateContent", content);
        self.close();
      }.bind(self));
    },
    del: function (content) {
      var self = this;
      this._post("/delete", content, function () {
        Hub.$emit("deleteContent", content);
        self.close();
      }.bind(self));
    },
    _post: function (url, content, after) {
      axios.post(url, JSON.stringify(content))
        .then(function (response) {
          after();
        })
        .catch(function (error) {
          console.log(error);
        });
    }
  },
  mounted() {
    this.$nextTick(
      function () {
        Hub.$on("open-modal", this.open);
        Hub.$on("close-modal", this.close);
      }.bind(this)
    );
  }
});