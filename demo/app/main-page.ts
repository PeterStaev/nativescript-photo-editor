import { NavigatedData, Page } from "ui/page";
import { Image } from "ui/image";
import { ImageSource, fromFileOrResource, fromFile } from "image-source";
import * as frame from "ui/frame";

import { PhotoEditor, PhotoEditorControl } from "nativescript-photo-editor";

let resultImage: Image;
let originalImage: Image;

export function navigatingTo(args: NavigatedData) {
    resultImage = (args.object as Page).getViewById<Image>("result-image");
    originalImage = (args.object as Page).getViewById<Image>("orig-image");
}

export function editImage() {
    const photoEditor = new PhotoEditor();
    
    photoEditor.editPhoto({
        imageSource: fromFileOrResource("~/test-image.jpg"), // originalImage.imageSource,
        hiddenControls: [
            // PhotoEditorControl.Save,
            // PhotoEditorControl.Clear,
            // PhotoEditorControl.Draw,
            // PhotoEditorControl.Text,
        ],
    }).then((newImage: ImageSource) => {
        resultImage.imageSource = newImage;
    }).catch((e) => {
        console.error(e);
    });
}