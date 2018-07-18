/*! *****************************************************************************
Copyright (c) 2018 Tangra Inc.
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
import * as application from "application";
import { path, knownFolders, File } from "file-system";
import { ImageSource, fromFile } from "image-source";

import { EditPhotoOptions, PhotoEditor as PhotoEditorBase, PhotoEditorControl } from ".";

declare const com: any;

export class PhotoEditor implements PhotoEditorBase {
    private static readonly EDIT_PHOTO_REQUEST = 9090;

    private _sourceTempFilePath: string;
    private _currentResolve: (result: ImageSource) => void;
    private _currentReject: (e: Error) => void;

    public editPhoto(options: EditPhotoOptions) {
        options.hiddenControls = options.hiddenControls || [];

        application.android.on("activityResult", this.onActivityResult);

        return new Promise<ImageSource>((resolve, reject) => {
            this._currentResolve = resolve;
            this._currentReject = reject;
            this._sourceTempFilePath = path.join(knownFolders.temp().path, `${(new Date()).getTime()}.jpg`);

            options.imageSource.saveToFile(this._sourceTempFilePath, "jpg");
            
            const intent = new android.content.Intent(application.android.foregroundActivity, com.tangrainc.photoeditor.PhotoEditorActivity.class);
            intent.putExtra("selectedImagePath", this._sourceTempFilePath);
            intent.putExtra("isCropIn", options.hiddenControls.indexOf(PhotoEditorControl.Crop) === -1);
            intent.putExtra("isDrawIn", options.hiddenControls.indexOf(PhotoEditorControl.Draw) === -1);
            intent.putExtra("isTextIn", options.hiddenControls.indexOf(PhotoEditorControl.Text) === -1);
            intent.putExtra("isSaveIn", false);
            intent.putExtra("isClearIn", options.hiddenControls.indexOf(PhotoEditorControl.Clear) === -1);
            application.android.foregroundActivity.startActivityForResult(intent, PhotoEditor.EDIT_PHOTO_REQUEST);    
        });
    }

    // NOTE: Intentionally using lambda function we get a "good" this!
    private onActivityResult = (args: application.AndroidActivityResultEventData) => {
        if (args.requestCode === PhotoEditor.EDIT_PHOTO_REQUEST) {
            switch (args.resultCode) {
                case android.app.Activity.RESULT_OK:
                    const resultIntent: android.content.Intent = args.intent;
                    const imagePath = resultIntent.getExtras().getString("imagePath");
                    const imageSource = fromFile(imagePath);

                    // Cleanup target temp file
                    File.fromPath(imagePath).removeSync();

                    this._currentResolve(imageSource);
                    break;

                case android.app.Activity.RESULT_CANCELED:
                    this._currentReject(new Error("User cancelled edit."));
                    break;

                default:
                    this._currentReject(new Error(`Photo Editor Result was: ${args.resultCode}`));
                    break;
            }

            // Cleanup source temp file
            if (this._sourceTempFilePath && File.exists(this._sourceTempFilePath)) {
                File.fromPath(this._sourceTempFilePath).removeSync();
            }

            // Cleanup events & current vars
            this._sourceTempFilePath = undefined;
            this._currentResolve = undefined;
            this._currentReject = undefined;
            application.android.off("activityResult", this.onActivityResult);
        }
    }
}