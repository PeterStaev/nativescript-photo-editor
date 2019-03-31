/*! *****************************************************************************
Copyright (c) 2019 Tangra Inc.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
***************************************************************************** */
import { ImageSource } from "image-source";
import * as frame from "ui/frame";

import { EditPhotoOptions, PhotoEditor as PhotoEditorBase, PhotoEditorControl } from ".";

export class PhotoEditor implements PhotoEditorBase {
    private _bundle: NSBundle;
    private _delegate: PhotoEditorDelegateImpl;

    constructor() {
        this._bundle = NSBundle.bundleForClass(PhotoEditorViewController.class());
    }

    public editPhoto(options: EditPhotoOptions) {
        const viewController = PhotoEditorViewController.alloc().initWithNibNameBundle("PhotoEditorViewController", this._bundle);
        const nativeHiddenControls: control[] = [control.Sticker, control.Share, control.Save];

        options.hiddenControls = options.hiddenControls || [];
        
        for (const hiddenControl of options.hiddenControls) {
            switch (hiddenControl) {
                case PhotoEditorControl.Crop:
                    nativeHiddenControls.push(control.Crop);
                    break;

                case PhotoEditorControl.Draw:
                    nativeHiddenControls.push(control.Draw);
                    break;

                case PhotoEditorControl.Text:
                    nativeHiddenControls.push(control.Text);
                    break;

                // case PhotoEditorControl.Save:
                //     nativeHiddenControls.push(control.Save);
                //     break;

                case PhotoEditorControl.Clear:
                    nativeHiddenControls.push(control.Clear);
                    break;

                default:
                    throw new Error(`Unknown control sent: ${hiddenControl}!`);
            }
        }

        return new Promise<ImageSource>((resolve, reject) => {
            this._delegate = PhotoEditorDelegateImpl.initWithResolveReject(resolve, reject);

            viewController.image = options.imageSource.ios;
            viewController.hiddenControls = nativeHiddenControls as any;
            viewController.photoEditorDelegate = this._delegate;

            frame.topmost().ios.controller.presentViewControllerAnimatedCompletion(viewController, true, null);
        });
    }
}

@ObjCClass(PhotoEditorDelegate)
class PhotoEditorDelegateImpl extends NSObject implements PhotoEditorDelegate {
    private _resolve: (imagesSource: ImageSource) => void;
    private _reject: (e: Error) => void;


    public static initWithResolveReject(resolve: (imageSource: ImageSource) => void, reject: (e: Error) => void): PhotoEditorDelegateImpl {
        const delegate = PhotoEditorDelegateImpl.new() as PhotoEditorDelegateImpl;

        delegate._resolve = resolve;
        delegate._reject = reject;

        return delegate;
    }


    public canceledEditing() {
        this._reject(new Error("User cancelled edit."));
    }

    public doneEditingWithImage(image: UIImage) {
        const result = new ImageSource();
        
        result.setNativeSource(image);
        
        this._resolve(result);        
    }

}