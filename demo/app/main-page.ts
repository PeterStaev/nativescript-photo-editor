import { NavigatedData, Page } from "ui/page";
import { Image } from "ui/image";

let resultImage: Image;

export function navigatingTo(args: NavigatedData) {
    resultImage = (args.object as Page).getViewById<Image>("result-image");
}

export function editImage() {
    console.log("Edit Image");
}