var Hub = new Vue();

new Vue({
  el: "#app2",
  data() {
    return {
      contents: []
    };
  },
  created() {
    this.$nextTick(function() {
      this.getData();
    });
  },
  methods: {
    getData: function() {
      const self = this;
      axios
        .get("/contents")
        .then(function(response) {
          self.contents = response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    openModal: function(content) {
      Hub.$emit("open-modal", content.content);
    },
    closeModal: function() {
      Hub.$emit("close-modal");
    }
  }
});

Vue.component("modal", {
  template: "#modal-template",
  data: function() {
    return {
      active: false,
      editContent: {}
    };
  },
  methods: {
    open: function(content) {
      this.active = true;
      this.editContent = {
        id: content.id,
        title: content.title,
        imageLink: content.imageLink,
        isbnCode: content.isbnCode,
        publisher: content.publisher
      };

    },
    close: function() {
      this.active = false;
    },
    update: function(content) {
      var self = this;
      axios
        .post("/edit", JSON.stringify(this.editContent))
        .then(function(response) {
          self.close();
        })
        .catch(function(error) {
          console.log(error);
        });
    }
  },
  mounted() {
    this.$nextTick(
      function() {
        Hub.$on("open-modal", this.open);
        Hub.$on("close-modal", this.close);
      }.bind(this)
    );
  }
});
