function () {
    var imgs = document.getElementsByTagName("img");
    var list = new Array();
    for(var i = 0; i < imgs.length; i++){
        list[i] = imgs[i].src;
    }
    for(var i = 0; i < imgs.length; i++){
        imgs[i].onclick = function() {
            showPhotos.showPhotosInGallery(this.src, list);
        }
    }
}