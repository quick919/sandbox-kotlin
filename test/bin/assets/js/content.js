var vm = new Vue({
  el: "#app",
  // data: {
  //   contentName: 'a',
  //   publisher: 'a',
  //   isActive: true
  // },
  data() {
    return {
      contentName: "",
      publisher: "",
      isActive: false,
      thumbnails: [],
      activeNumber: ""
    };
  },
  methods: {
    post: function() {
      console.log(this.publisher);
      axios
        .post("/content", {
          name: this.contentName,
          publisher: this.publisher
        })
        .then(function(response) {
          console.log(response);
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    fetchThumbnail: function() {
      const self = this;
      axios
        .get("/thumbnail", {
          params: {
            name: this.contentName
          }
        })
        .then(function(response) {
          console.log(response.data);
          console.log(self.thumbnails);
          self.thumbnails = response.data;
          console.log(self.thumbnails);
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    selectThumbnail: function(index) {
      const self = this;
      console.log(self.activeNumber);
      console.log(index);
      // console.log(self.thumbnails[index]);
      if (self.activeNumber === "") {
        var obj = self.thumbnails[index];
        obj.isActive = true;
        self.activeNumber = index;
        self.thumbnails.splice(index, 1, obj);
      } else {
        if (self.activeNumber === index) {
          var obj = self.thumbnails[self.activeNumber];
          obj.isActive = false;
          self.activeNumber = "";
          self.thumbnails.splice(self.activeNumber, 1, obj);
        } else {
          var obj = self.thumbnails[self.activeNumber];
          obj.isActive = false;
          self.thumbnails.splice(self.activeNumber, 1, obj);
          self.activeNumber = "";
          var obj = self.thumbnails[index];
          obj.isActive = true;
          self.activeNumber = index;
          self.thumbnails.splice(index, 1, obj);
        }
      }
      console.log(self.thumbnails);
    }
  }
});
