#!/bin/bash

brew install emacs

# Create init.el if it doesn't exist
mkdir -p ~/.emacs.d

cat <<EOF > ~/.emacs.d/init.el
(require 'package)
(setq package-enable-at-startup nil)
(add-to-list 'package-archives
             '("melpa" . "https://melpa.org/packages/") t)
(package-initialize)

(unless (package-installed-p 'use-package)
  (package-refresh-contents)
  (package-install 'use-package))

(require 'use-package)
(setq use-package-always-ensure t)

(use-package rust-mode
  :hook (rust-mode . lsp-deferred))

(use-package lsp-mode
  :commands lsp
  :custom
  (lsp-rust-server 'rust-analyzer))

(use-package lsp-ui
  :commands lsp-ui-mode)
  
;; Supress unnecessary doc string warnings                                                                                                                                                                                                                                                  
(when (listp byte-compile-warnings)
  (setq byte-compile-warnings (remove 'docstrings byte-compile-warnings)))
      
;; Smooth scrolling
(setq scroll-margin 10)           ;; start scrolling when 10 lines from edge
(setq scroll-conservatively 101) ;; don't recenter cursor unnecessarily
(setq scroll-step 1)             ;; scroll one line at a time
(setq auto-window-vscroll nil)   ;; disable slow pixel-based scrolling

;; Quickly reload init.el                                                                                                                                                                                                                                                                   
(defun reload-init-file ()
  "Reload the Emacs init file."
  (interactive)
  (load-file user-init-file))

(global-set-key (kbd "C-c r") 'reload-init-file)

EOF

