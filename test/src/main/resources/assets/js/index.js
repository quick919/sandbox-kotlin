var Hub = new Vue();

new Vue({
  el: "#app2",
  data() {
    return {
      contents: []
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
      axios
        .get("/contents")
        .then(function (response) {
          self.contents = response.data;
        })
        .catch(function (error) {
          console.log(error);
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
    }
  }
});

Vue.component("modal", {
  template: "#modal-template",
  data: function () {
    return {
      active: false,
      editContent: {},
      options: [{
        id: 1,
        text: "Amazon"
      }, {
        id: 2,
        text: "O'Reilly"
      }, {
        id: 3,
        text: "gihyo"
      }, {
        id: 4,
        text: "Leanpub"
      }]
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
      axios
        .post("/edit", JSON.stringify(this.editContent))
        .then(function (response) {
          Hub.$emit("updateContent", content);
          self.close();
        })
        .catch(function (error) {
          console.log(error);
        });
    },
    del: function (content) {
      var self = this;
      axios
        .post("/delete", JSON.stringify(this.editContent))
        .then(function (response) {
          Hub.$emit("deleteContent", content);
          self.close();
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