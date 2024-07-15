import "codemirror";

export interface ModeInfo {
    name: string;
    mime?: string | undefined;
    mimes?: string[] | undefined;
    mode: string;
    file?: RegExp | undefined;
    ext?: string[] | undefined;
    alias?: string[] | undefined;
}

declare module "codemirror" {
    const modeInfo: ModeInfo[];
    function findModeByMIME(mime: string): ModeInfo | undefined;
    function findModeByExtension(ext: string): ModeInfo | undefined;
    function findModeByFileName(filename: string): ModeInfo | undefined;
    function findModeByName(name: string): ModeInfo | undefined;
}
export default function use(cm: typeof import('codemirror')): void;