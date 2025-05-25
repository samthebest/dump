#!/bin/bash

brew install emacs
rustup component add rust-analyzer

# Create init.el if it doesn't exist
mkdir -p ~/.emacs.d

# MANUAL SHIT
# Open Terminal > Preferences > Profiles > Keyboard
# Check “Use Option as Meta key”

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

;; Quickly edit init.el
(defun find-user-init-file ()
  "Edit the `user-init-file`, which is usually ~/.emacs.d/init.el."
  (interactive)
  (find-file user-init-file))

(setq select-enable-clipboard t)

;; Directory view thing
(setq treemacs-width 70)

(use-package treemacs
  :ensure t
  :defer t
  :bind
  ("C-c t" . treemacs)
  :config)

;; Make copy paste actually work (in GUI apparently (not tested))                                                                                                                                                                                                                           
(setq select-enable-clipboard t)

;; Make copy paste actually work in Emacs                                                                                                                                                                                                                                                   
(setq interprogram-cut-function
      (lambda (text &optional _push)
        (let ((process-connection-type nil))
          (let ((proc (start-process "pbcopy" "*Messages*" "pbcopy")))
            (process-send-string proc text)
            (process-send-eof proc)))))

(setq interprogram-paste-function
      (lambda ()
        (let ((text (shell-command-to-string "pbpaste")))
          (unless (string= text "")
            text))))

;; Delete line                                                                                                                                                                                                                                                                              
(global-set-key (kbd "C-c x") 'kill-whole-line)

;; Copy text                                                                                                                                                                                                                                                                                
(global-set-key (kbd "C-c c") 'kill-ring-save)

;; Auto save                                                                                                                                                                                                                                                                                
(setq auto-save-timeout 2)
(setq auto-save-interval 10)
(setq auto-save-default t)

(run-with-idle-timer 2 t (lambda () (save-some-buffers t)))

;; Remove indentation                                                                                                                                                                                                                                                                       
(defun unindent-region-by-one-level (start end)
  "Unindent selected region by one standard indent level for the current major mode."
  (interactive "r")
  (let ((indent (or (and (boundp 'c-basic-offset) c-basic-offset)
                    (and (boundp 'lisp-body-indent) lisp-body-indent)
                    (and (boundp 'rust-indent-offset) rust-indent-offset)
                    tab-width
                    4)))
    (indent-rigidly start end (- indent))))

(global-set-key (kbd "<backtab>") 'unindent-region-by-one-level)

;; Add indentation                                                                                                                                                                                                                                                                          
(defun indent-region-by-one-level ()
  "Indent selected region by one standard indent level for the current major mode."
  (interactive)
  (let ((indent (or (and (boundp 'c-basic-offset) c-basic-offset) ; c-like modes                                                                                                                                                                                                            
                    (and (boundp 'lisp-body-indent) lisp-body-indent) ; elisp                                                                                                                                                                                                               
                    (and (boundp 'rust-indent-offset) rust-indent-offset) ; rust-mode                                                                                                                                                                                                       
                    tab-width ; fallback                                                                                                                                                                                                                                                    
                    4))) ; default fallback                                                                                                                                                                                                                                                 
    (indent-rigidly (region-beginning) (region-end) indent)))

(global-set-key (kbd "TAB") 'indent-region-by-one-level)

;; Make text selection stay selected (shit not really work way)                                                                                                                                                                                                                             
;; (defun keep-region-active (&rest _args)                                                                                                                                                                                                                                                  
;;   "Keep the region active after the command."                                                                                                                                                                                                                                            
;;   (setq deactivate-mark nil))                                                                                                                                                                                                                                                            

;; (advice-add 'comment-region :after #'keep-region-active)                                                                                                                                                                                                                                 
;; (advice-add 'indent-region :after #'keep-region-active)                                                                                                                                                                                                                                  
;; ;; Add your custom commands here, for example:                                                                                                                                                                                                                                           
;; (advice-add 'unindent-region-by-one-level :after #'keep-region-active)                                                                                                                                                                                                                   
;; (advice-add 'indent-region-by-one-level :after #'keep-region-active)                                                                                                                                                                                                                     


;; Brute force make selection fucking work                                                                                                                                                                                                                                                  
(setq deactivate-mark nil)
(add-hook 'post-command-hook
          (lambda ()
            (when (region-active-p)
              (setq deactivate-mark nil))))



;; Save backups to                                                                                                                                                                                                                                    
(setq backup-directory-alist
      `(("." . ,(concat user-emacs-directory ".emacs-backups"))))



EOF

